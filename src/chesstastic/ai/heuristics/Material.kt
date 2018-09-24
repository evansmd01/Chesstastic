package chesstastic.ai.heuristics

import chesstastic.ai.Constants
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color.Light

class Material(private val constants: Constants): Heuristic {
    override fun evaluate(board: Board): Score {
        var light = 0.0
        var dark = 0.0

        // TODO: REFACTOR TO DETERMINE FROM POSITIONAL METADATA
        Board.SQUARES.forEach {
            val piece = board[it]
            if (piece != null) {
                val value = constants.pieceValue(piece.kind)
                if (piece.color == Light) light += value else dark += value
            }
        }

        return Score(light, dark)
    }
}

