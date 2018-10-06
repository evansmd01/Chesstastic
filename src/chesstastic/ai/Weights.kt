package chesstastic.ai

import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.entities.*
import chesstastic.ai.Weights.Key.*

class Weights(private val overrides: Map<String, Double> = emptyMap()) {
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

        // pawn promotion
        PAWN_PROMOTION,
        PROMOTION_1_RANK_AWAY,
        PROMOTION_2_RANKS_AWAY,
        PROMOTION_3_RANKS_AWAY,

        // attacking
        WINNING_THE_EXCHANGE,
        EXCHANGE_GAIN_BONUS,
        EXCHANGE_SIMPLIFICATION_BONUS,

        // mobility
        MOBILITY,

        // development
        DEVELOPMENT,
        KNIGHT_DEVELOPMENT_BONUS,
        BISHOP_DEVELOPMENT_BONUS,
        ROOK_DEVELOPMENT_BONUS,

        // king pressure
        KING_PRESSURE,
        KING_PRESSURE_ATTACK_RATIO_BONUS,
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
            CENTRAL_OCCUPANT_SCORE -> 2.0
            CENTRAL_ATTACK_SCORE -> 1.0

            // castling
            CASTLING -> 1.0
            QUEENSIDE_CASTLE_BONUS -> 4.0
            KINGSIDE_CASTLE_BONUS -> 5.0
            CANNOT_CASTLE_PENALTY -> 5.0

            // pawn promotion
            PAWN_PROMOTION -> 1.0
            PROMOTION_1_RANK_AWAY -> 4.0
            PROMOTION_2_RANKS_AWAY -> 2.0
            PROMOTION_3_RANKS_AWAY -> 1.0

            //attacking
            WINNING_THE_EXCHANGE -> 5.0
            EXCHANGE_GAIN_BONUS -> 1.0
            EXCHANGE_SIMPLIFICATION_BONUS -> 1.0

            // mobility
            MOBILITY -> 0.5

            // development
            DEVELOPMENT -> 2.0
            KNIGHT_DEVELOPMENT_BONUS -> 3.0
            BISHOP_DEVELOPMENT_BONUS -> 2.0
            ROOK_DEVELOPMENT_BONUS -> 1.0

            // king pressure
            KING_PRESSURE -> 1.0
            KING_PRESSURE_ATTACK_RATIO_BONUS -> 10.0
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
