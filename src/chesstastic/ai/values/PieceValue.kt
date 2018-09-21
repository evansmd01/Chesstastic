package chesstastic.ai.values

import chesstastic.engine.entities.*
import chesstastic.engine.entities.PieceKind.*

object PieceValue {
    fun find(piece: PieceKind) = when (piece) {
        Pawn -> 1.0
        Bishop -> 3.2
        Knight -> 3.0
        Rook -> 5.0
        Queen -> 9.0
        King -> 0.0
    }
}
