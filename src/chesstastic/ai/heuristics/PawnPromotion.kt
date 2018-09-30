package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.ai.heuristics.models.Imbalance
import chesstastic.engine.entities.*

class PawnPromotion(override val weights: Weights): Heuristic {
    override val key = PAWN_PROMOTION

    override fun calculateImbalance(board: Board): Imbalance {
        var light = 0.0
        var dark = 0.0

        board.metadata.lightPlayer.pawns.forEach {
            when (it.square.rank) {
                Rank._7 -> light += weights[PROMOTION_1_RANK_AWAY]
                Rank._6 -> light += weights[PROMOTION_2_RANKS_AWAY]
                Rank._5 -> light += weights[PROMOTION_3_RANKS_AWAY]
                else -> { }
            }
        }
        board.metadata.darkPlayer.pawns.forEach {
            when (it.square.rank) {
                Rank._2 -> dark += weights[PROMOTION_1_RANK_AWAY]
                Rank._3 -> dark += weights[PROMOTION_2_RANKS_AWAY]
                Rank._4 -> dark += weights[PROMOTION_3_RANKS_AWAY]
                else -> { }
            }
        }
        return Imbalance(light, dark)
    }
}
