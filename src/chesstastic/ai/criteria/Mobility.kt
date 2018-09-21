package chesstastic.ai.criteria

import chesstastic.ai.values.Score
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color

object Mobility: Criteria {
    private val weight = 0.5 // todo: get from config
    override fun evaluate(board: Board): Score {
//        val lightMoves = BoardCalculator.legalMoves(board, Color.Light).count()
//        val darkMoves = BoardCalculator.legalMoves(board, Color.Dark).count()
        // TODO: figure out whats going wrong here ^^^

        return when (board.turn) {
            Color.Light -> Score(board.legalMoves.count(), 0)
            Color.Dark -> Score(0, board.legalMoves.count())
        }

    }
}
