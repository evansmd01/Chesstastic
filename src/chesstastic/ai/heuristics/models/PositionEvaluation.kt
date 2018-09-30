package chesstastic.ai.heuristics.models

import chesstastic.engine.entities.Color

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
