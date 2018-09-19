package chesstastic.ai.criteria

import chesstastic.ai.values.PieceWeight
import chesstastic.ai.values.Score
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color

object Material: Criteria {
    override fun evaluate(board: Board): Score {
        var light = 0.0
        var dark = 0.0

        Board.SQUARES.forEach {
            val piece = board[it]
            if (piece != null) {
                val value = PieceWeight.find(piece.kind)
                if (piece.color == Color.Light) light += value else dark += value
            }
        }

        return Score(light, dark)
    }
}

