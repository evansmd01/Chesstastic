package chesstastic.ai.heuristics

import chesstastic.ai.Constants
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color

interface Heuristic {
    val key: Constants.Key
    val constants: Constants

    fun calculateBaseScore(board: Board): Score

    fun evaluate(board: Board) = HeuristicSummary(
        key = key,
        baseScore = calculateBaseScore(board),
        modifier = constants[key]
    )

    companion object {
        val factories = setOf<(Constants) -> Heuristic>(
            { Material(it) },
            { ControlOfCenter(it) }
        )
    }
}

data class PositionEvaluation(
    val winner: Color?,
    val stalemate: Boolean,
    val heuristics: List<HeuristicSummary>
) {
    val finalScore by lazy {
        when {
            winner != null -> Score.checkmate(winner)
            stalemate -> Score.even
            else -> heuristics.fold(Score(1.0,1.0)) { score, heuristic ->
                score + heuristic.effectiveScore
            }
        }
    }
}

data class HeuristicSummary(
    val key: Constants.Key,
    val baseScore: Score,
    val modifier: Double
) {
    val effectiveScore = baseScore * modifier
}



