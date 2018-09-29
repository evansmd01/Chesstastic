package chesstastic.ai.heuristics

import chesstastic.ai.Constants
import chesstastic.ai.Constants.Key.*
import chesstastic.engine.entities.*

class ControlOfCenter(override val constants: Constants): Heuristic {
    override val key = CONTROL_OF_CENTER

    override fun calculateBaseScore(board: Board): Score {
        var light = 0.0
        var dark = 0.0

        for (rank in 3..4) {
            for (file in 3..4) {
                val squareMeta = board.metadata.squares[Square(File.fromIndex(file)!!, Rank.fromIndex(rank)!!)]!!
                when (squareMeta.occupant?.color) {
                    Color.Light ->
                        light += constants[CENTRAL_OCCUPANT_SCORE]
                    Color.Dark ->
                        dark += constants[CENTRAL_OCCUPANT_SCORE]
                }
                squareMeta.isAttackedBy.forEach { when(it.piece.color) {
                    Color.Light ->
                        light += constants[CENTRAL_ATTACK_SCORE]
                    Color.Dark ->
                        dark += constants[CENTRAL_ATTACK_SCORE]
                } }
            }
        }

        return Score.fromImbalance(light, dark)
    }
}
