package chesstastic.ai

import chesstastic.ai.heuristics.*
import chesstastic.ai.models.*
import chesstastic.engine.entities.*

interface AIPlayer {
    fun selectMove(board: Board): Move
}

class Chesstastic private constructor (
    private val depth: Int,
    private val breadth: Int,
    private val heuristics: Set<Heuristic>
): AIPlayer {
    var lastBranchChosen: BranchEvaluation? = null

    override fun selectMove(board: Board): Move {
        lastBranchChosen = findBestBranch(board.historyMetadata.currentTurn, board, depth, breadth)
        return lastBranchChosen?.branch?.firstOrNull()
            ?: throw Exception("Could not find a move")
    }

    private fun findBestBranch(player: Color, board: Board, depth: Int, breadth: Int, previous: BranchEvaluation? = null): BranchEvaluation? {
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
        heuristics = heuristics.map { it.evaluate(board) }
    )

    companion object {
        val DEFAULT = configured {  }
        fun configured(apply: ChesstasticConfig.() -> Unit): Chesstastic {
            val config = ChesstasticConfig()
            apply(config)
            return Chesstastic(
                depth = config.depth,
                breadth = config.breadth,
                heuristics = config.heuristics
            )
        }
    }
}

data class ChesstasticConfig(
    var depth: Int = 2,
    var breadth: Int = 2,
    var heuristics: Set<Heuristic> = Heuristic.factories.map { it(Weights()) }.toSet()
)
