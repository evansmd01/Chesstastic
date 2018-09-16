package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*
import chesstastic.engine.rules.MoveCalculator

class PawnMoveCalculator {
    companion object: PieceMoveCalculator {

        private fun rankDelta(color: Color) = if (color == Color.Light) 1 else -1
        private fun enPassantRank(color: Color): Rank = if (color == Color.Light) Rank._5 else Rank._4
        private fun promotionRank(color: Color): Rank = if (color == Color.Light) Rank._8 else Rank._1

        override fun legalMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> {
            return potentialMoves(color, fromSquare, board).filterNot { move ->
                MoveCalculator.isKingInCheck(color, board.updated(move))
            }
        }

        /**
         * Moves that have not been validated for legality, but which obey the movement abilities of the piece
         */
        private fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> {
            val forwardMoves = mutableListOf<Move>()
            val rankDelta = rankDelta(color)
            val promotionRank = promotionRank(color)

            // try to move forward
            val forwardOne = fromSquare.transform(0, rankDelta)!!
            var isBlocked = board[forwardOne] != null
            if (!isBlocked) {
                // promotion
                if (forwardOne.rank == promotionRank) {
                    forwardMoves.add(Move.Promotion(fromSquare, forwardOne, Queen(color)))
                    forwardMoves.add(Move.Promotion(fromSquare, forwardOne, Knight(color)))
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
            val attackingSquares = listOfNotNull(fromSquare.transform(1, rankDelta), fromSquare.transform(-1, rankDelta))
            val captureMoves = attackingSquares.flatMap { target ->
                val isOccupiedByOpponentPiece = board[target]?.color == color.opposite
                val isPromotable = target.rank == promotionRank
                when {
                    isOccupiedByOpponentPiece && isPromotable -> listOf(
                        Move.Promotion(fromSquare, target, Queen(color)),
                        Move.Promotion(fromSquare, target, Knight(color))
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
            return board.history.last() == enPassantOpening(color, target.file)
        }

        private fun enPassantOpening(color: Color, file: File): Move = Move.Basic(
            from = Square(file, startingRank(color.opposite)),
            to = Square(file, enPassantRank(color)))

        private fun startingRank(color: Color) = if (color == Color.Light) Rank._2 else Rank._7
    }
}
