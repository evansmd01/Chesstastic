package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Square
import chesstastic.engine.entities.Knight
import chesstastic.engine.entities.Move

class KnightMoveCalculator(val piece: Knight, val currentCoord: Square, val board: Board): PieceMoveCalculator {

    override val attackingSquares: Iterable<Square> by lazy {
        listOf<Square>()
    }

    override val legalMoves: Iterable<Move> by lazy {
        listOf<Move>()
    }
}
