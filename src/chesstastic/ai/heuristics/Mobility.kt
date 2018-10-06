package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.ai.models.Imbalance
import chesstastic.engine.entities.Board

class Mobility(override val weights: Weights): Heuristic {
    override val key = MOBILITY

    override fun calculateImbalance(board: Board): Imbalance {
        return Imbalance(
            board.metadata.lightPlayer.moves.all.size.toDouble(),
            board.metadata.darkPlayer.moves.all.size.toDouble())
    }
}
