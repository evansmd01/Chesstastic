package chesstastic.ai

import chesstastic.ai.heuristics.*
import chesstastic.ai.heuristics.models.PositionEvaluation
import chesstastic.ai.heuristics.models.Score
import chesstastic.engine.entities.*
import java.lang.Exception

interface AIPlayer {
    fun selectMove(board: Board): Move
}

class Chesstastic(
    private val depth: Int = 3,
    private val breadth: Int = 3,
    private val weights: Weights = Weights(emptyMap()),
    heuristicFactories: Set<(Weights) -> Heuristic> = Heuristic.factories
): AIPlayer {
    private val heuristics = heuristicFactories.map { it(weights) }

    override fun selectMove(board: Board): Move =
        findBestBranch(board.historyMetadata.currentTurn, board, depth, breadth)?.branch?.move
            ?: throw Exception("Could not find a move")

    private fun findBestBranch(player: Color, board: Board, depth: Int, breadth: Int, previous: Evaluation? = null): Evaluation? {
        val currentTurn = board.historyMetadata.currentTurn
        val moves = board.metadata.legalMoves.shuffled()
        // stop recursion if we've hit depth, ensuring we ended with an opponent response
        // , or if there are no legal moves left
        if ((depth < 0 && currentTurn == player) || moves.isEmpty()) {
            return previous
        }

        val bestMovesForCurrentPlayer = moves
            .asSequence()
            .map { move ->
                val updatedBoard = board.updated(move)
                val branch = previous?.branch?.plus(move) ?: Branch(move)
                Evaluation(branch, evaluate(updatedBoard).finalScore, updatedBoard)
            }
            .sortedByDescending { it.score.ratioInFavorOf(currentTurn) }
            .toList()


        val bestEvaluations = when (currentTurn) {
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

    fun evaluate(board: Board) = PositionEvaluation(
        winner = if (board.metadata.isCheckmate) board.historyMetadata.currentTurn.opposite else null,
        stalemate = board.metadata.isStalemate,
        heuristics = heuristics.map { it.evaluate(board) }
    )
}

private data class Branch(val move: Move, val next: Branch? = null) {
    operator fun plus(other: Move) = Branch(move, Branch(other))
}

private data class Evaluation(val branch: Branch, val score: Score, val board: Board) {
    override fun toString(): String {
        return "score: ${score.ratioInFavorOf(Color.Light)}, ${board.historyMetadata.history}"
    }
}
