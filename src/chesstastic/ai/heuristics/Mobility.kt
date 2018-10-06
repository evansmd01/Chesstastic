package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.ai.models.Score
import chesstastic.engine.entities.Board

class Mobility(override val weights: Weights): Heuristic {
    override val key = MOBILITY

    override fun calculateBaseScore(board: Board): Score {
        return Score(
            board.metadata.lightPlayer.moves.all.size.toDouble(),
            board.metadata.darkPlayer.moves.all.size.toDouble())
    }
}
