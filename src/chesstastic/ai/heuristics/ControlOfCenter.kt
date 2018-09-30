package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.ai.heuristics.models.Imbalance
import chesstastic.engine.entities.*

class ControlOfCenter(override val weights: Weights): Heuristic {
    override val key = CONTROL_OF_CENTER

    override fun calculateImbalance(board: Board): Imbalance {
        var light = 0.0
        var dark = 0.0

        for (rank in 3..4) {
            for (file in 3..4) {
                val squareMeta = board.metadata.squares[Square(File.fromIndex(file)!!, Rank.fromIndex(rank)!!)]!!
                when (squareMeta.occupant?.color) {
                    Color.Light ->
                        light += weights[CENTRAL_OCCUPANT_SCORE]
                    Color.Dark ->
                        dark += weights[CENTRAL_OCCUPANT_SCORE]
                }
                squareMeta.isAttackedBy.forEach { when(it.piece.color) {
                    Color.Light ->
                        light += weights[CENTRAL_ATTACK_SCORE]
                    Color.Dark ->
                        dark += weights[CENTRAL_ATTACK_SCORE]
                } }
            }
        }

        return Imbalance(light, dark)
    }
}

