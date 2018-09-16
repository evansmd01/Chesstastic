package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*

interface DiagonalMoveCalculator: PieceMoveCalculator {
    override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> {
        return listOf()
    }

    override fun timesSquareIsAttacked(target: Square, attacker: Color, board: Board): Int {
        return 0
    }

    fun isCorrectPiece(piece: Piece): Boolean
}
