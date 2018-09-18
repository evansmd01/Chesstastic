package chesstastic.ai.values

import chesstastic.engine.entities.*

object PieceWeight {
    fun find(piece: Piece) = when (piece) {
        is Pawn -> 1.0
        is Bishop -> 3.2
        is Knight -> 3.0
        is Rook -> 5.0
        is Queen -> 9.0
        is King -> 0.0
    }
}
