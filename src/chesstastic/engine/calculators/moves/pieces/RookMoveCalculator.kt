package chesstastic.engine.calculators.moves.pieces

import chesstastic.engine.calculators.moves.HorizontalMoveCalculator
import chesstastic.engine.entities.*

class RookMoveCalculator {
    companion object: HorizontalMoveCalculator {
        override fun isCorrectPiece(piece: Piece): Boolean = piece is Rook
    }
}
