package chesstastic.ai.heuristics.models

import chesstastic.ai.Weights

data class HeuristicResult(
    val key: Weights.Key,
    val imbalance: Imbalance,
    val weight: Double
) {
    val weightedScore = imbalance.score * weight
}
