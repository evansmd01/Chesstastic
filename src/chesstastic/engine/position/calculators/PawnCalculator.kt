package chesstastic.engine.position.calculators

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.entities.PieceKind.*

object PawnCalculator: MoveCalculator, AttackCalculator {
    private fun rankDelta(color: Color) = if (color == Light) 1 else -1
    private fun enPassantRank(color: Color): Rank = if (color == Light) _5 else _4
    private fun promotionRank(color: Color): Rank = if (color == Light) _8 else _1

    override fun attackers(target: Square, attacker: Color, board: Board): List<Pair<Piece,Square>> {
        val rankDelta = rankDelta(attacker.opposite)
        val attackOrigin1 = target.transform(fileDelta = 1, rankDelta = rankDelta)
        val attackOrigin2 = target.transform(fileDelta = -1, rankDelta = rankDelta)

        return listOfNotNull(attackOrigin1, attackOrigin2)
            .mapNotNull { square ->
                val piece = board[square]
                if (piece?.color == attacker && piece.kind == Pawn)
                    piece to square
                else null
            }
    }

    override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> {
        val rankDelta = rankDelta(color)
        val promotionRank = promotionRank(color)

        // try to move forward
        val forwardMoves = mutableListOf<Move>()
        val forwardOne = fromSquare.transform(0, rankDelta)!!
        var isBlocked = board[forwardOne] != null
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
                isBlocked = board[forwardTwo] != null
                if (!isBlocked) forwardMoves.add(Move.Basic(fromSquare, forwardTwo))
            }
        }

        // try to capture diagonals
        val captureMoves = listOfNotNull(
            fromSquare.transform(1, rankDelta),
            fromSquare.transform(-1, rankDelta)
        ).flatMap { target ->
            val isOccupiedByOpponentPiece = board[target]?.color == color.opposite
            val isPromotable = target.rank == promotionRank
            when {
                isOccupiedByOpponentPiece && isPromotable -> listOf(
                    Move.Promotion(fromSquare, target, Queen),
                    Move.Promotion(fromSquare, target, Knight)
                )
                isOccupiedByOpponentPiece -> listOf(Move.Basic(fromSquare, target))
                isEnPassantEligible(color, fromSquare, target, board) ->
                    listOf(Move.EnPassant(fromSquare, target))
                else -> listOf()
            }
        }

        return forwardMoves + captureMoves
    }

    private fun isEnPassantEligible(color: Color, fromSquare: Square, target: Square, board: Board): Boolean {
        if (fromSquare.rank != enPassantRank(color)) return false
        return board.historyMetadata.history.mostRecent == enPassantOpening(color, target.file)
    }

    private fun enPassantOpening(color: Color, file: File): Move = Move.Basic(
        from = Square(file, startingRank(color.opposite)),
        to = Square(file, enPassantRank(color)))

    private fun startingRank(color: Color) = if (color == Light) _2 else _7
}
