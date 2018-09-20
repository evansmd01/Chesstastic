package chesstastic.ai

import chesstastic.ai.criteria.*
import chesstastic.ai.values.Score
import chesstastic.engine.entities.*
import java.lang.Exception

class ChesstasticAI(private vararg val criteria: Criteria) {

    fun selectMove(
        board: Board,
        depth: Int,
        breadth: Int): Move =
        findBestBranch(board.turn, board, depth, breadth)?.branch?.move ?:
        throw Exception("Could not find a move")

    private fun findBestBranch(player: Color, board: Board, depth: Int, breadth: Int, previous: Evaluation? = null): Evaluation? {
        val currentTurn = board.turn

        // stop recursion if we've hit depth, ensuring we ended with an opponent response
        // , or if there are no legal moves left
        if((depth < 0 && currentTurn == player) || board.legalMoves.count() == 0) {
            return previous
        }

        val bestMovesForCurrentPlayer = board.legalMoves
            .map { move ->
                val updatedBoard = board.updated(move)
                val branch = previous?.branch?.plus(move) ?: Branch(move)
                Evaluation(branch, evaluate(updatedBoard), updatedBoard)
            }
            .sortedByDescending { it.score.ratioInFavorOf(currentTurn) }


        val bestEvaluations = when(currentTurn) {
            player -> bestMovesForCurrentPlayer.take(breadth)
            else -> bestMovesForCurrentPlayer.take(1)
        }

        return bestEvaluations
            .map { evaluation ->
                val narrowerBreadth = if (breadth > 1) breadth - 1 else 1
                findBestBranch(player, evaluation.board, depth - 1, narrowerBreadth, evaluation)!!
            }
            .sortedByDescending { it.score.ratioInFavorOf(currentTurn) }
            .first()
    }

    private fun evaluate(board: Board): Score =
        criteria.asSequence().map { it.evaluate(board) }
            .fold(Score.even) { total, score -> total + score }
}

private data class Branch(val move: Move, val next: Branch? = null) {
    operator fun plus(other: Move) = Branch(move, Branch(other))
}

private data class Evaluation(val branch: Branch, val score: Score, val board: Board)
