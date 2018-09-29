package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.engine.entities.Board

class PinsAndSkewers(override val weights: Weights): Heuristic {
    override val key = PINS_AND_SKEWERS

    override fun calculateBaseScore(board: Board): Score {
        var light = 0.0
        var dark = 0.0

        board.metadata.lightPlayer.moves.pins.forEach { pin ->
            light += weights.pieceValue(pin.pinned.piece.kind) * weights[PIN_BONUS]
        }
        board.metadata.darkPlayer.moves.pins.forEach { pin ->
            dark += weights.pieceValue(pin.pinned.piece.kind) * weights[PIN_BONUS]
        }

        board.metadata.lightPlayer.moves.skewers.forEach { skewer ->
            val lesserPieceValue = Math.min(
                weights.pieceValue(skewer.skewered.piece.kind),
                weights.pieceValue(skewer.to.piece.kind)
            )
            light += lesserPieceValue * weights[SKEWER_BONUS]
        }
        board.metadata.darkPlayer.moves.skewers.forEach { skewer ->
            val lesserPieceValue = Math.min(
                weights.pieceValue(skewer.skewered.piece.kind),
                weights.pieceValue(skewer.to.piece.kind)
            )
            dark += lesserPieceValue * weights[SKEWER_BONUS]
        }

        return Score.fromImbalance(light, dark)
    }
}
