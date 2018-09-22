package chesstastic.ai.criteria

import chesstastic.ai.values.Constants
import chesstastic.ai.values.Constants.Companion.Key.*
import chesstastic.ai.values.Score
import chesstastic.engine.entities.*

class Development(private val constants: Constants): Criteria {
    override fun evaluate(board: Board): Score {
        // Get number of squares from back rank that have originated at least one move
        // Max count at 7, because moving the king is only a good thing if it's to castle
        // And castling woudn't count as a move originating from the rook's corner.
        val light = distinctStartingSquares(board, Rank._1).count()
        val dark = distinctStartingSquares(board, Rank._8).count()

        return Score(Math.max(light, 7), Math.max(dark, 7)) * constants[DEVELOPMENT_WEIGHT]
    }

    /**
     * The unique set of squares from which a move has originated on the back rank
     * Each square that has originated at least one move represents a first move for a piece
     */
    private fun distinctStartingSquares(board: Board, rank: Rank): Set<Square> =
        board.history.map { it.from }.filter { it.rank == rank }.toSet()
}
