package chesstastic.engine.entities.metadata.moves

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.entities.metadata.*

object PawnMoves {
    private fun rankDelta(color: Color) = if (color == Light) 1 else -1
    private fun enPassantRank(color: Color): Rank = if (color == Light) _5 else _4
    private fun promotionRank(color: Color): Rank = if (color == Light) _8 else _1

    fun calculate(
        color: Color,
        fromSquare: Square,
        pieces: Map<Square, Piece>,
        historyMetadata: HistoryMetadata
    ): Iterable<Move> {
        val rankDelta = rankDelta(color)
        val promotionRank = promotionRank(color)

        // try to move forward
        val forwardMoves = mutableListOf<Move>()
        val forwardOne = fromSquare.transform(0, rankDelta)!!
        var isBlocked = pieces[forwardOne] != null
        if (!isBlocked) {
            // promotion
            if (forwardOne.rank == promotionRank) {
                forwardMoves.add(Move.Promotion(fromSquare, forwardOne, Queen))
                forwardMoves.add(Move.Promotion(fromSquare, forwardOne, Knight))
            } else
                forwardMoves.add(Move.Basic(fromSquare, forwardOne))

            // Double starting move
            if (fromSquare.rank == startingRank(color)) {
                val forwardTwo = forwardOne.transform(0, rankDelta)!!
                isBlocked = pieces[forwardTwo] != null
                if (!isBlocked) forwardMoves.add(Move.Basic(fromSquare, forwardTwo))
            }
        }

        // try to capture diagonals
        val captureMoves = listOfNotNull(
            fromSquare.transform(1, rankDelta),
            fromSquare.transform(-1, rankDelta)
        ).flatMap { target ->
            val isOccupiedByOpponentPiece = pieces[target]?.color == color.opposite
            val isPromotable = target.rank == promotionRank
            when {
                isOccupiedByOpponentPiece && isPromotable -> listOf(
                    Move.Promotion(fromSquare, target, Queen),
                    Move.Promotion(fromSquare, target, Knight)
                )
                isOccupiedByOpponentPiece -> listOf(Move.Basic(fromSquare, target))
                isEnPassantEligible(color, fromSquare, target, historyMetadata) ->
                    listOf(Move.EnPassant(fromSquare, target))
                else -> listOf()
            }
        }

        return forwardMoves + captureMoves
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
