package chesstastic.ai.criteria

import chesstastic.ai.values.Score
import chesstastic.engine.calculators.BoardCalculator
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color

object MovesAvailable: Criteria {
    override fun evaluate(board: Board): Score {
        val moves = BoardCalculator.legalMoves(board, board.turn).count()
        // todo: figure out why opposite color causes king move to get swapped funny
        // then add back the move calculations for the opposite player to get a better ratio

        return when (board.turn) {
            Color.Light -> Score(moves.toDouble(), 0.0)
            Color.Dark -> Score(0.0, moves.toDouble())
        } * 3.0
    }
}
