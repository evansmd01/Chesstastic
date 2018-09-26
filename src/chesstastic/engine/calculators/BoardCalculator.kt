package chesstastic.engine.calculators

import chesstastic.engine.entities.*

// TODO: REPLACE THIS CLASS COMPLETELY WITH BoardMetadata implementation.
class BoardCalculator {
    companion object {
        fun legalMoves(board: Board, color: Color): Iterable<Move> {
            val potentialMoves = Board.SQUARES.flatMap { square ->
                val piece = board[square]
                if (piece?.color == color) {
                    MoveCalculator.getBy(piece.kind).potentialMoves(color, square, board)
                } else listOf()
            }
            return potentialMoves.filterNot { move ->
                isKingInCheck(color, board.updatedWithoutValidation(move))
            }
        }

        fun findAttackers(target: Square, attacker: Color, board: Board): List<Pair<Piece, Square>> {
            return AttackCalculator.all.flatMap { it.attackers(target, attacker, board) }
        }

        fun isSquareAttacked(target: Square, attacker: Color, board: Board): Boolean {
            return AttackCalculator.all.any { it.attackers(target, attacker, board).isNotEmpty() }
        }

        fun isKingInCheck(color: Color, board: Board): Boolean {
            return isSquareAttacked(target = board.kingSquare(color), attacker = color.opposite, board = board)
        }
    }
}

