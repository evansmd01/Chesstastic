package chesstastic.ai

import chesstastic.ai.heuristics.*
import chesstastic.ai.models.*
import chesstastic.engine.entities.*

interface AIPlayer {
    fun selectMove(board: Board): Move
}

class Chesstastic(private val config: ChesstasticConfig = ChesstasticConfig.DEFAULT): AIPlayer {
    var lastBranchChosen: BranchEvaluation? = null

    override fun selectMove(board: Board): Move {
        lastBranchChosen = findBestBranch(board.historyMetadata.currentTurn, board, config.depth, config.breadth)
        return lastBranchChosen?.branch?.firstOrNull()
            ?: throw Exception("Could not find a move")
    }

    private fun findBestBranch(player: Color, board: Board, depth: Int, breadth: Int, previous: BranchEvaluation? = null): BranchEvaluation? {
        val currentTurn = board.historyMetadata.currentTurn
        val moves = board.metadata.legalMoves
        // stop recursion if we've hit depth, ensuring we ended with an opponent response
        // , or if there are no legal moves left
        if ((depth < 0 && currentTurn == player) || moves.isEmpty()) {
            return previous
        }

        val bestMovesForCurrentPlayer = moves
            .asSequence()
            .map { move ->
                val updatedBoard = board.updated(move)
                val branch = previous?.branch?.plus(move) ?: listOf(move)
                BranchEvaluation(branch, evaluate(updatedBoard).finalScore, updatedBoard)
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
                findBestBranch(player, evaluation.board, depth - 1, breadth, evaluation)!!
            }
            .sortedByDescending { it.score.ratioInFavorOf(currentTurn) }
            .toList()
        return bestBranches.first()
    }

    fun evaluate(board: Board) = PositionEvaluation(
        winner = if (board.metadata.isCheckmate) board.historyMetadata.currentTurn.opposite else null,
        stalemate = board.metadata.isStalemate,
        heuristics = config.heuristics.map { it.evaluate(board) }
    )
}

data class ChesstasticConfig(val depth: Int, val breadth: Int, val heuristics: Set<Heuristic> = emptySet()) {
    companion object {
        val DEFAULT = ChesstasticConfig(2, 2, Heuristic.factories.map { it(Weights()) }.toSet())
    }
}
