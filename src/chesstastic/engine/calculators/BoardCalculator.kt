package chesstastic.engine.calculators

import chesstastic.engine.entities.*
import chesstastic.engine.calculators.moves.*

class BoardCalculator {
    companion object {
        fun legalMoves(board: Board, color: Color): Iterable<Move> {
            val potentialMoves = Board.SQUARES.flatMap { square ->
                val piece = board[square]
                if (piece?.color == color) {
                    PieceMoveCalculator.getBy(piece).potentialMoves(color, square, board)
                } else listOf()
            }
            return potentialMoves.filterNot { move ->
                isKingInCheck(color, board.updated(move))
            }
        }

        fun timesSquareIsAttacked(target: Square, attacker: Color, board: Board): Int {
            return PieceMoveCalculator.all.sumBy { it.timesSquareIsAttacked(target, attacker, board) }
        }

        fun isSquareAttacked(target: Square, attacker: Color, board: Board): Boolean {
            return PieceMoveCalculator.all.any { it.timesSquareIsAttacked(target, attacker, board) > 0 }
        }

        fun isKingInCheck(color: Color, board: Board): Boolean {
            return isSquareAttacked(target = board.kingSquare(color), attacker = color.opposite, board = board)
        }
    }
}

