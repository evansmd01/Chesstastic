package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Coordinate
import chesstastic.engine.entities.Move
import chesstastic.engine.entities.Queen

class QueenMoveCalculator(val piece: Queen, val currentCoord: Coordinate, val board: Board): PieceMoveCalculator {

    override val coordinatesUnderAttack: Iterable<Coordinate> by lazy {
        listOf<Coordinate>()
    }

    override val legalMoves: Iterable<Move> by lazy {
        listOf<Move>()
    }
}
