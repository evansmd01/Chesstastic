package chesstastic.ai

import chesstastic.ai.heuristics.*
import chesstastic.ai.Constants.Companion.Key.*
import chesstastic.ai.heuristics.Score
import chesstastic.engine.entities.*
import java.lang.Exception

interface AIPlayer {
    fun selectMove(board: Board): Move
}

class Chesstastic(
    private val depth: Int,
    private val breadth: Int,
    private val constants: Constants = Constants(emptyMap()),
    heuristicFactories: List<(Constants) -> Heuristic> = Heuristic.factories
): AIPlayer {
    private val heuristics = heuristicFactories.map { it(constants) }

    override fun selectMove(board: Board): Move =
        findBestBranch(board.historyMetadata.currentTurn, board, depth, breadth)?.branch?.move ?:
        throw Exception("Could not find a move")

    private fun findBestBranch(player: Color, board: Board, depth: Int, breadth: Int, previous: Evaluation? = null): Evaluation? {
        val currentTurn = board.historyMetadata.currentTurn
        val moves = board.legalMoves.shuffled()
        // stop recursion if we've hit depth, ensuring we ended with an opponent response
        // , or if there are no legal moves left
        if((depth < 0 && currentTurn == player) || moves.isEmpty()) {
            return previous
        }

        val bestMovesForCurrentPlayer = moves
            .asSequence()
            .map { move ->
                val updatedBoard = board.updated(move)
                val branch = previous?.branch?.plus(move) ?: Branch(move)
                Evaluation(branch, evaluate(updatedBoard), updatedBoard)
            }
            .sortedByDescending { it.score.ratioInFavorOf(currentTurn) }
            .toList()


        val bestEvaluations = when(currentTurn) {
            player -> bestMovesForCurrentPlayer.take(breadth)
            else -> bestMovesForCurrentPlayer.take(1)
        }

        val bestBranches = bestEvaluations
            .asSequence()
            .map { evaluation ->
                val narrowerBreadth = if (breadth > depth) breadth - 1 else 1
                findBestBranch(player, evaluation.board, depth - 1, narrowerBreadth, evaluation)!!
            }
            .sortedByDescending { it.score.ratioInFavorOf(currentTurn) }
            .toList()
        return bestBranches.first()
    }

    private fun evaluate(board: Board): Score = when {
        board.isCheckmate -> Score.forOnly(board.historyMetadata.currentTurn.opposite, Double.POSITIVE_INFINITY)
        board.isStalemate -> Score.even
        else -> heuristics.asSequence().map { it.evaluate(board) }
            .fold(Score.even) { total, score -> total + score }
    }
}

private data class Branch(val move: Move, val next: Branch? = null) {
    operator fun plus(other: Move) = Branch(move, Branch(other))
}

private data class Evaluation(val branch: Branch, val score: Score, val board: Board) {
    override fun toString(): String {
        return "score: ${score.ratioInFavorOf(Color.Light)}, ${board.historyMetadata.history}"
    }
}
