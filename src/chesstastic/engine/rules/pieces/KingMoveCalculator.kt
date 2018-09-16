package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Square
import chesstastic.engine.entities.King
import chesstastic.engine.entities.Move

class KingMoveCalculator(val piece: King, val currentCoord: Square, val board: Board): PieceMoveCalculator {

    override val attackingSquares: Iterable<Square> by lazy {
        listOf<Square>()
    }

    override val legalMoves: Iterable<Move> by lazy {
        listOf<Move>()
    }
}
