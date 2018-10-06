package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.models.HeuristicResult
import chesstastic.ai.models.Score
import chesstastic.engine.entities.Board

interface Heuristic {
    val key: Weights.Key
    val weights: Weights

    fun calculateBaseScore(board: Board): Score

    fun evaluate(board: Board) = HeuristicResult(
        key = key,
        baseScore = calculateBaseScore(board),
        weight = weights[key]
    )

    companion object {
        val factories = setOf<(Weights) -> Heuristic>(
            { Material(it) },
            { ControlOfCenter(it) },
            { Castling(it) },
            { PawnPromotion(it) },
            { Exchanges(it) },
            { Mobility(it) }
        )
    }
}



