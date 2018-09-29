package chesstastic.ai.heuristics

import chesstastic.ai.Constants
import chesstastic.ai.Constants.Key.*
import chesstastic.engine.entities.Board

class PinsAndSkewers(override val constants: Constants): Heuristic {
    override val key = PINS_AND_SKEWERS

    override fun calculateBaseScore(board: Board): Score {
        var light = 0.0
        var dark = 0.0

        board.metadata.lightPlayer.moves.pins.forEach { pin ->
            light += constants.pieceValue(pin.pinned.piece.kind) * constants[PIN_BONUS]
        }
        board.metadata.darkPlayer.moves.pins.forEach { pin ->
            dark += constants.pieceValue(pin.pinned.piece.kind) * constants[PIN_BONUS]
        }

        board.metadata.lightPlayer.moves.skewers.forEach { skewer ->
            val lesserPieceValue = Math.min(
                constants.pieceValue(skewer.skewered.piece.kind),
                constants.pieceValue(skewer.to.piece.kind)
            )
            light += lesserPieceValue * constants[SKEWER_BONUS]
        }
        board.metadata.darkPlayer.moves.skewers.forEach { skewer ->
            val lesserPieceValue = Math.min(
                constants.pieceValue(skewer.skewered.piece.kind),
                constants.pieceValue(skewer.to.piece.kind)
            )
            dark += lesserPieceValue * constants[SKEWER_BONUS]
        }

        return Score.fromImbalance(light, dark)
    }
}
