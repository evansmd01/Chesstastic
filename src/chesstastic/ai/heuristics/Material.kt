package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.ai.models.Score
import chesstastic.engine.entities.Board

class Material(override val weights: Weights): Heuristic {
    override val key = MATERIAL

    override fun calculateBaseScore(board: Board): Score {
        val light =  board.metadata.lightPlayer.allPieces
            .map { weights.pieceValue(it.piece.kind) }
            .sum()
        val dark =  board.metadata.darkPlayer.allPieces
            .map { weights.pieceValue(it.piece.kind) }
            .sum()

        return Score(light, dark)
    }
}


