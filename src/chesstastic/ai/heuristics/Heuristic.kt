package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.heuristics.models.HeuristicResult
import chesstastic.ai.heuristics.models.Imbalance
import chesstastic.ai.heuristics.models.Score
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color

interface Heuristic {
    val key: Weights.Key
    val weights: Weights

    fun calculateImbalance(board: Board): Imbalance

    fun evaluate(board: Board) = HeuristicResult(
        key = key,
        imbalance = calculateImbalance(board),
        weight = weights[key]
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
    val heuristics: List<HeuristicResult>
) {
    val finalScore by lazy {
        when {
            winner != null -> Score.checkmate(winner)
            stalemate -> Score.EVEN
            else -> heuristics.fold(Score(1.0, 1.0)) { score, heuristic ->
                score + heuristic.weightedScore
            }
        }
    }
}



