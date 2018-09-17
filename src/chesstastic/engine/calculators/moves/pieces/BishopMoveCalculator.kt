package chesstastic.engine.calculators.moves.pieces

import chesstastic.engine.calculators.moves.DiagonalMoveCalculator
import chesstastic.engine.entities.*

class BishopMoveCalculator {
    companion object: DiagonalMoveCalculator {
        override fun isCorrectPiece(piece: Piece): Boolean = piece is Bishop
    }
}
