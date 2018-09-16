package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*

class RookMoveCalculator {
    companion object: HorizontalMoveCalculator {
        override fun isCorrectPiece(piece: Piece): Boolean = piece is Rook
    }
}
