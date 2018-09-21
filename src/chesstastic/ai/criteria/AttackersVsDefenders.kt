package chesstastic.ai.criteria

import chesstastic.ai.values.PieceValue
import chesstastic.ai.values.Score
import chesstastic.engine.calculators.BoardCalculator
import chesstastic.engine.entities.*

object AttackersVsDefenders: Criteria {
    val weight = 5 // TODO: get this from configuration

    override fun evaluate(board: Board): Score {
        var lightBalance = 0.0
        var darkBalance = 0.0

        Board.SQUARES.forEach { square ->
            val piece = board[square]
            when {
                piece?.color == Color.Light -> lightBalance += getBalance(Color.Light, square, board)
                piece?.color == Color.Dark -> darkBalance += getBalance(Color.Light, square, board)
            }
        }

        return Score(lightBalance, darkBalance)
    }

    private fun getBalance(defenderColor: Color, square: Square, board: Board): Double {
        val attackers = BoardCalculator.findAttackers(square, attacker = defenderColor.opposite, board = board)
            .map { it.first }

        // "attacking" your own piece is defending it, because you're prepared to recapture on that square
        val defenders = BoardCalculator.findAttackers(square, attacker = defenderColor, board = board)
            .map { it.first }

        // You want to have more defence on your piece than the opponent has attacks
        // So a positive score is one in which defence is higher
        return quantityToValueRatio(defenders) - quantityToValueRatio(attackers)
    }

    /**
     * It's better to have a few defenders worth little value (pawns)
     * Then to have many defenders worth lots of value (queens and rooks)
     * Because you don't actually want to get forced to recapture
     * with a high value piece, exposing it to threats
     *
     * This ratio makes a single pawn defense worth 1.  (1 quantity / 1 pieceValue)
     * While a queen defence is worth almost nothing:  (1 quantity / 9 pieceValue)
     */
    private fun quantityToValueRatio(pieces: List<Piece>): Double {
        val count = pieces.size
        val value = pieces.map { PieceValue.find(it.kind) }.sum()
        return count / value
    }
}
