package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.File.*
import chesstastic.engine.entities.Color.*

class RookMoveCalculator {
    companion object: PieceMoveCalculator {
        override fun timesSquareIsAttacked(target: Square, attacker: Color, board: Board): Int {
            return 0
        }

        override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> {
            return listOf()
        }

    }
}
