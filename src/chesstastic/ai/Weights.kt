package chesstastic.ai

import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.entities.*
import chesstastic.ai.Weights.Key.*

class Weights(private val overrides: Map<String, Double>) {
    enum class Key {
        // Heuristic Weights
        MATERIAL,
        QUEEN_MATERIAL_VALUE,
        KING_MATERIAL_VALUE,
        PAWN_MATERIAL_VALUE,
        ROOK_MATERIAL_VALUE,
        KNIGHT_MATERIAL_VALUE,
        BISHOP_MATERIAL_VALUE,

        // control of center
        CONTROL_OF_CENTER,
        CENTRAL_OCCUPANT_SCORE,
        CENTRAL_ATTACK_SCORE,

        // castling
        CASTLING,
        QUEENSIDE_CASTLE_BONUS,
        KINGSIDE_CASTLE_BONUS,
        CANNOT_CASTLE_PENALTY,

        // pins & skewers
        PINS_AND_SKEWERS,
        PIN_BONUS,
        SKEWER_BONUS,
    }

    companion object {
        fun default(key: Key): Double = when (key) {
            // Heuristic Weights
            MATERIAL -> 10.0
            QUEEN_MATERIAL_VALUE -> 9.0
            KING_MATERIAL_VALUE -> 0.0
            PAWN_MATERIAL_VALUE -> 1.0
            ROOK_MATERIAL_VALUE -> 5.0
            KNIGHT_MATERIAL_VALUE -> 3.0
            BISHOP_MATERIAL_VALUE -> 3.0

            // control of center
            CONTROL_OF_CENTER -> 1.0
            CENTRAL_OCCUPANT_SCORE -> 1.0
            CENTRAL_ATTACK_SCORE -> 1.0

            // castling
            CASTLING -> 1.0
            QUEENSIDE_CASTLE_BONUS -> 4.0
            KINGSIDE_CASTLE_BONUS -> 5.0
            CANNOT_CASTLE_PENALTY -> 5.0

            // pins and skewers
            PINS_AND_SKEWERS -> 1.0
            PIN_BONUS -> 5.0
            SKEWER_BONUS -> 5.0
        }
    }

    operator fun get(key: Key): Double = overrides[key.name] ?: default(key)

    fun pieceValue(pieceKind: PieceKind): Double = when(pieceKind) {
        Queen -> this[QUEEN_MATERIAL_VALUE]
        King -> this[KING_MATERIAL_VALUE]
        Rook -> this[ROOK_MATERIAL_VALUE]
        Bishop -> this[BISHOP_MATERIAL_VALUE]
        Knight -> this[KNIGHT_MATERIAL_VALUE]
        Pawn -> this[PAWN_MATERIAL_VALUE]
    }
}
