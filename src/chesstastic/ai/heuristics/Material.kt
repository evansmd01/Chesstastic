package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.ai.models.Imbalance
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color.*

class Material(override val weights: Weights): Heuristic {
    override val key = MATERIAL

    override fun calculateImbalance(board: Board): Imbalance {
        var light = 0.0
        var dark = 0.0

        // TODO: REFACTOR TO DETERMINE FROM POSITIONAL METADATA
        Board.SQUARES.forEach {
            val piece = board[it]
            if (piece != null) {
                val value = weights.pieceValue(piece.kind)
                if (piece.color == Light) light += value else dark += value
            }
        }

        return Imbalance(light, dark)
    }
}

