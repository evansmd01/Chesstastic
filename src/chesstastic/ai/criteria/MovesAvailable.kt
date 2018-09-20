package chesstastic.ai.criteria

import chesstastic.ai.values.Score
import chesstastic.engine.calculators.BoardCalculator
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color

object MovesAvailable: Criteria {
    override fun evaluate(board: Board): Score {
        val playerMoves = BoardCalculator.legalMoves(board, board.turn).count()
        val opponentMoves = 0//BoardCalculator.legalMoves(board, board.turn.opposite).count()

        return when (board.turn) {
            Color.Light -> Score(playerMoves.toDouble(), opponentMoves.toDouble())
            Color.Dark -> Score(opponentMoves.toDouble(), playerMoves.toDouble())
        } * 3.0 // todo: weights come from configuration
    }
}
