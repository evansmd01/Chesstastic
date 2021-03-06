package chesstastic.engine.metadata.calculation.potential

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.metadata.*

object PotentialPawnMoves {
    private fun rankDelta(color: Color) = if (color == Light) 1 else -1
    private fun enPassantRank(color: Color): Rank = if (color == Light) _5 else _4
    private fun promotionRank(color: Color): Rank = if (color == Light) _8 else _1

    fun calculate(
        color: Color,
        fromSquare: Square,
        getPiece: (Square) -> Piece?,
        historyMetadata: HistoryMetadata
    ): List<MoveMetadata> {
        val rankDelta = rankDelta(color)
        val promotionRank = promotionRank(color)

        // try to move forward
        val forwardMoves = mutableListOf<MoveMetadata>()
        val forwardOne = fromSquare.transform(0, rankDelta)!!
        var isBlocked = getPiece(forwardOne) != null
        if (!isBlocked) {
            // promotion
            if (forwardOne.rank == promotionRank) {
                forwardMoves.add(
                    MoveMetadata(Move.Promotion(fromSquare, forwardOne, Queen), Piece(Pawn, color), null, null))
                forwardMoves.add(MoveMetadata(
                    Move.Promotion(fromSquare, forwardOne, Knight), Piece(Pawn, color), null, null))
            } else
                forwardMoves.add(
                    MoveMetadata(Move.Basic(fromSquare, forwardOne), Piece(Pawn, color), null, null))

            // Double starting move
            if (fromSquare.rank == startingRank(color)) {
                val forwardTwo = forwardOne.transform(0, rankDelta)!!
                isBlocked = getPiece(forwardTwo) != null
                if (!isBlocked) forwardMoves.add(
                    MoveMetadata(Move.Basic(fromSquare, forwardTwo), Piece(Pawn, color), null, null))
            }
        }

        // try to capture diagonals
        val diagonalMoves = listOfNotNull(
            fromSquare.transform(1, rankDelta),
            fromSquare.transform(-1, rankDelta)
        ).flatMap { target ->
            val occupant = getPiece(target)?.let { PieceMetadata(it, target) }
            val capturing = if (occupant?.piece?.color == color.opposite) occupant else null
            val supporting = if (occupant?.piece?.color == color) occupant else null
            when {
                isEnPassantEligible(color, fromSquare, target, historyMetadata) -> {
                    val move = Move.EnPassant(fromSquare, target)
                    listOf(MoveMetadata(move, Piece(Pawn, color), PieceMetadata(Piece(Pawn, color.opposite), move.captured), null))
                }
                target.rank == promotionRank -> listOf(
                    MoveMetadata(Move.Promotion(fromSquare, target, Queen), Piece(Pawn, color),
                        capturing = capturing, supporting = supporting),
                    MoveMetadata(Move.Promotion(fromSquare, target, Knight), Piece(Pawn, color),
                        capturing = capturing, supporting = supporting)
                )
                else -> listOf(MoveMetadata(Move.Basic(fromSquare, target), Piece(Pawn, color),
                    capturing = capturing, supporting = supporting))
            }
        }

        return forwardMoves + diagonalMoves
    }

    private fun isEnPassantEligible(
        color: Color, fromSquare: Square, target: Square, historyMetadata: HistoryMetadata
    ): Boolean {
        if (fromSquare.rank != enPassantRank(color)) return false
        return historyMetadata.history.mostRecent == enPassantOpening(color, target.file)
    }

    private fun enPassantOpening(color: Color, file: File): Move = Move.Basic(
        from = Square(file, startingRank(color.opposite)),
        to = Square(file, enPassantRank(color)))

    private fun startingRank(color: Color) = if (color == Light) _2 else _7
}
