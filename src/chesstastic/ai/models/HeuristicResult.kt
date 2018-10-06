package chesstastic.ai.models

import chesstastic.ai.Weights

data class HeuristicResult(
    val key: Weights.Key,
    val baseScore: Score,
    val weight: Double
) {
    val weightedScore = baseScore * weight
}
