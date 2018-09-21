package chesstastic.ai.criteria

import chesstastic.ai.values.Score
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color
import chesstastic.engine.entities.Move

object Castling: Criteria {
    private val lightCastles = setOf(Move.KingsideCastle(Color.Light), Move.QueensideCastle(Color.Light))
    private val darkCastles = setOf(Move.KingsideCastle(Color.Dark), Move.QueensideCastle(Color.Dark))
    val weight = 10 // todo: get from config

    override fun evaluate(board: Board): Score {
        // TODO: DOCK POINTS IF YOU LOST YOUR CHANCE TO CASTLE BY MOVING KING OR ROOK
        var lightCastled = false
        var darkCastled = false
        for (move in board.history) {
            if (move in lightCastles) lightCastled = true
            else if (move in darkCastles) darkCastled = true
            if (lightCastled && darkCastled) break
        }
        return Score(if (lightCastled) weight else 0, if (darkCastled) weight else 0)
    }

}
