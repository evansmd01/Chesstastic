package chesstastic.engine.rules

import chesstastic.engine.entities.*
import chesstastic.engine.rules.pieces.*

class MoveCalculator {
    companion object {
        fun legalMoves(board: Board): Iterable<Move> {
            return coordinates.flatMap { coord ->
                val piece = board[coord]
                if (piece?.color == board.turn) {
                    PieceMoveCalculator.new(piece, coord, board).legalMoves
                } else listOf()
            }
        }

        fun isCoordinateInCheck(targetCoord: Coordinate, attacker: Color, board: Board): Boolean =
            coordinates.any { fromCoord ->
                val piece = board[fromCoord]
                if (piece?.color == attacker) {
                    val attacks = PieceMoveCalculator.new(piece, fromCoord, board).coordinatesUnderAttack
                    targetCoord in attacks
                }
                false
            }


        fun isKingInCheck(color: Color, board: Board): Boolean {
            val kingLocation = coordinates.find { coord ->
                val piece = board[coord]
                when {
                    piece is King && piece.color == color -> true
                    else -> false
                }
            }
            return kingLocation?.let {
                isCoordinateInCheck(targetCoord = it, attacker = color.opposite, board = board)
            } ?: throw Error("King was not on the board")
        }

        private val coordinates: List<Coordinate> by lazy {
            File.values().flatMap { file ->
                Rank.values().map { rank ->
                    Coordinate(file, rank)
                }
            }
        }
    }
}

