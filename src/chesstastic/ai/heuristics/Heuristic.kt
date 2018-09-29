package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color

interface Heuristic {
    val key: Weights.Key
    val weights: Weights

    fun calculateBaseScore(board: Board): Score

    fun evaluate(board: Board) = HeuristicSummary(
        key = key,
        baseScore = calculateBaseScore(board),
        modifier = weights[key]
    )

    companion object {
        val factories = setOf<(Weights) -> Heuristic>(
            { Material(it) },
            { ControlOfCenter(it) },
            { Castling(it) },
            { PinsAndSkewers(it) }
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
    val key: Weights.Key,
    val baseScore: Score,
    val modifier: Double
) {
    val effectiveScore = baseScore * modifier
}



