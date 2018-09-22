package chesstastic.ai.criteria

import chesstastic.ai.values.Constants
import chesstastic.ai.values.Score
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color.Light

class Material(private val constants: Constants): Criteria {
    override fun evaluate(board: Board): Score {
        var light = 0.0
        var dark = 0.0

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

