package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.File.*
import chesstastic.engine.entities.Color.*

class KnightMoveCalculator {
    companion object: PieceMoveCalculator {
        override fun timesSquareIsAttacked(target: Square, attacker: Color, board: Board): Int =
            squaresInRange(target)
                .count {
                    val piece = board[it]
                    piece is Knight && piece.color == attacker
                }

        override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> =
            squaresInRange(fromSquare)
                .filterNot { board.isOccupiedByColor(it, color) }
                .map { Move.Basic(fromSquare, it) }

        private fun squaresInRange(fromSquare: Square): Iterable<Square> = listOfNotNull(
            fromSquare.transform(2, -1),
            fromSquare.transform(2, 1),
            fromSquare.transform(-2, -1),
            fromSquare.transform(-2, 1),
            fromSquare.transform(1, -2),
            fromSquare.transform(-1, -2),
            fromSquare.transform(1, 2),
            fromSquare.transform(-1, 2)
        )
    }
}
