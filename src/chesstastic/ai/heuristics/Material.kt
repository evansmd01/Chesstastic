package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.ai.models.Imbalance
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color.*

class Material(override val weights: Weights): Heuristic {
    override val key = MATERIAL

    override fun calculateImbalance(board: Board): Imbalance {
        val light =  board.metadata.lightPlayer.allPieces
            .map { weights.pieceValue(it.piece.kind) }
            .sum()
        val dark =  board.metadata.darkPlayer.allPieces
            .map { weights.pieceValue(it.piece.kind) }
            .sum()

        return Imbalance(light, dark)
    }
}


