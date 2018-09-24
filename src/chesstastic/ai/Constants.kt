package chesstastic.ai

import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.entities.*
import chesstastic.ai.Constants.Companion.Key.*

class Constants(private val overrides: Map<String, Double>) {
    companion object {
        enum class Key {
            QUEEN_VALUE,
            KING_VALUE,
            PAWN_VALUE,
            ROOK_VALUE,
            KNIGHT_VALUE,
            BISHOP_VALUE,
            DEVELOPMENT_WEIGHT,
        }

        fun default(key: Key): Double = when (key) {
            QUEEN_VALUE -> 9.0
            KING_VALUE -> 0.0
            PAWN_VALUE -> 1.0
            ROOK_VALUE -> 5.0
            KNIGHT_VALUE -> 3.0
            BISHOP_VALUE -> 3.0
            DEVELOPMENT_WEIGHT -> 20.0
        }
    }

    operator fun get(key: Key): Double = overrides[key.name] ?: default(key)

    fun pieceValue(pieceKind: PieceKind): Double = when(pieceKind) {
        Queen -> this[QUEEN_VALUE]
        King -> this[KING_VALUE]
        Rook -> this[ROOK_VALUE]
        Bishop -> this[BISHOP_VALUE]
        Knight -> this[KNIGHT_VALUE]
        Pawn -> this[PAWN_VALUE]
    }
}
