package chesstastic.ai

import chesstastic.ai.heuristics.Heuristic
import chesstastic.ai.models.PositionEvaluation
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color
import chesstastic.engine.entities.Move

class Chesstastic(
    private val depth: Int,
    private val breadth: Int,
    private val heuristics: Set<Heuristic>
): AIPlayer {
    var lastBranchChosen: EvaluationTree? = null

    override fun selectMove(board: Board): Move {
        val tree = EvaluationTree.new(board, evaluate(board))
        // TODO: replace fixed depth with timeout based logic to continue evaluating until elaspsed time is up.
        (0..depth).forEach { _ -> updateBestLeafNodes(board.historyMetadata.currentTurn, tree) }

        lastBranchChosen = tree.leafNodes().maxBy { it.evaluation.score.ratioInFavorOf(board.historyMetadata.currentTurn) }
        return lastBranchChosen?.moves()?.firstOrNull() ?: throw Exception("Unable to select a move. \nTree: $tree \nboard: $board")
    }

    private fun updateBestLeafNodes(forPlayer: Color, evaluationTree: EvaluationTree) {
        val bestLeafNodes = evaluationTree.appendableLeafNodes().sortedByDescending { node: EvaluationTree.AppendableNode ->
            node.evaluation.score.ratioInFavorOf(forPlayer)
        }

        bestLeafNodes.take(breadth).forEach { node ->
            val bestMoves = node.board.metadata.legalMoves.map { move ->
                val updated = node.board.updatedWithoutValidation(move)
                val evaluation = evaluate(updated)
                Triple(move, updated, evaluation.score)
            }.sortedByDescending { (_, _, score) ->
                score.ratioInFavorOf(forPlayer)
            }

            bestMoves.take(breadth).forEach { (move, boardWithMove, _) ->
                if (boardWithMove.metadata.isGameOver) {
                    node.addChild(EvaluationTree.TerminatingNode(boardWithMove, evaluate(boardWithMove), move))
                } else {
                    // evaluate responses
                    val possibleResponses = boardWithMove.metadata.legalMoves.map { response ->
                        val boardWithResponse = boardWithMove.updatedWithoutValidation(response)
                        Triple(response, evaluate(boardWithResponse), boardWithResponse)
                    }

                    // take the response that results in the best possible score for the responder
                    val (response, evaluation, boardWithResponse) = possibleResponses.maxBy { (_, evaluation, _) ->
                        evaluation.score.ratioInFavorOf(forPlayer.opposite)
                    } ?: throw Exception("There were no possible responses. This should have been a terminating node. ${node.board}")

                    node.addChild(EvaluationTree.BranchingNode(
                        boardWithResponse, evaluation, move, response
                    ))
                }
            }
        }

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
