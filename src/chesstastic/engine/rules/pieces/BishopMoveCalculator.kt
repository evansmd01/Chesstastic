package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*

class BishopMoveCalculator {
    companion object: DiagonalMoveCalculator {
        override fun isCorrectPiece(piece: Piece): Boolean = piece is Bishop
    }
}
