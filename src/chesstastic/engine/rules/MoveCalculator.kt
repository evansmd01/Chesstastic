package chesstastic.engine.rules

import chesstastic.engine.entities.*
import chesstastic.engine.rules.pieces.*

class MoveCalculator {
    companion object {
        fun legalMoves(board: Board): Iterable<Move> {
            return Board.SQUARES.flatMap { coord ->
                val piece = board[coord]
                if (piece?.color == board.turn) {
                    PieceMoveCalculator.new(piece, coord, board).legalMoves
                } else listOf()
            }
        }

        fun isSquareAttacked(target: Square, attacker: Color, board: Board): Boolean {
            // TODO: HUGE OPTIMIZATION.
            // Start from the target square:
            //   - check corners for pawns
            //   - check diagonals for bishops or queens
            //   - check ranks & files for rooks or queens
            //   - check circumference for knights
            // This way instead of looping through all possible moves for all pieces,
            // we only loop through the limited possibilities for attacking this square
            Board.SQUARES.any { fromSquare ->
                val piece = board[fromSquare]
                if (piece?.color == attacker) {
                    val attacks = PieceMoveCalculator.new(piece, fromSquare, board).attackingSquares
                    target in attacks
                } else false
            }
        }


        fun isKingInCheck(color: Color, board: Board): Boolean {
            val kingLocation = Board.SQUARES.find { coord ->
                val piece = board[coord]
                when {
                    piece is King && piece.color == color -> true
                    else -> false
                }
            }
            return kingLocation?.let {
                isSquareAttacked(target = it, attacker = color.opposite, board = board)
            } ?: throw Error("King was not on the board")
        }


    }
}

