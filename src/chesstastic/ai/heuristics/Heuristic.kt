package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.models.HeuristicResult
import chesstastic.ai.models.Imbalance
import chesstastic.engine.entities.Board

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
            { PinsAndSkewers(it) },
            { PawnPromotion(it) }
        )
    }
}



