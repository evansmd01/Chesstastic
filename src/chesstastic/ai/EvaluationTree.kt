package chesstastic.ai

import chesstastic.ai.models.BranchEvaluation
import chesstastic.ai.models.PositionEvaluation
import chesstastic.ai.models.Score
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Move

abstract class EvaluationTree private constructor(
    val board: Board,
    val evaluation: PositionEvaluation,
    var ancestor: EvaluationTree?,
    val children: MutableSet<EvaluationTree>
) {
    fun moves(): List<Move> {
        return when (this) {
            is NodeWithMove -> listOf(move) + (ancestor?.moves() ?: emptyList())
            else -> emptyList()
        }
    }

    fun getBranchEvaluation(): BranchEvaluation = BranchEvaluation(moves(), evaluation.score, board)

    fun leafNodes(): List<EvaluationTree> =
        when {
            children.isEmpty() -> listOf(this)
            else -> children.flatMap { it.leafNodes() }
        }

    fun appendableLeafNodes(): List<EvaluationTree.AppendableNode> = leafNodes().mapNotNull { it as? AppendableNode }

    // region Nodes

    interface NodeWithMove {
        val move: Move
    }

    abstract class AppendableNode(board: Board, evaluation: PositionEvaluation): EvaluationTree(board, evaluation, null, mutableSetOf()) {
        fun addChild(child: EvaluationTree) {
            child.ancestor = this
            children.add(child)
        }
    }

    class RootNode(board: Board, evaluation: PositionEvaluation) : AppendableNode(board, evaluation)

    /**
     * A checkmate or stalemate. A leaf node that cannot be evaluated further.
     */
    class TerminatingNode(
        board: Board,
        evaluation: PositionEvaluation,
        override val move: Move
    ) : EvaluationTree(board, evaluation, null, mutableSetOf()), NodeWithMove

    class BranchingNode(
        board: Board,
        evaluation: PositionEvaluation,
        override val move: Move,
        val response: Move
    ) : AppendableNode(board, evaluation), NodeWithMove

    // endregion

    companion object {
        fun new(board: Board, evaluation: PositionEvaluation): EvaluationTree.RootNode =
            EvaluationTree.RootNode(board, evaluation)
    }
}
