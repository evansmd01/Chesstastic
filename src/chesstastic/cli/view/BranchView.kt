package chesstastic.cli.view

import chesstastic.ai.models.BranchEvaluation
import chesstastic.engine.entities.Move
import chesstastic.util.applyColor
import chesstastic.util.padded
import chesstastic.util.times

object BranchView: TableView() {

    fun render(branchEval: BranchEvaluation): String {
        val pairs = branchEval.branch.toPairs().toList()

        return "SELECTED BRANCH\n${100.times("-")}\n" + pairs.joinToString(
            prefix = indent + "MOVE".padded(10) +
                separator + "RESPONSE".padded(10) +
                "\n${100.times("-")}\n",
            separator = "\n",
            postfix = "\n${100.times("-")}\nEXPECTED SCORE: ${branchEval.score.format()}")
        {
            indent +
                it.first.toString()
                    .applyColor(branchEval.board.historyMetadata.currentTurn)
                    .padded(10) +
            separator +
                it.second.toString()
                    .applyColor(branchEval.board.historyMetadata.currentTurn.opposite)
        }
    }

    private fun List<Move>.toPairs(): Sequence<Pair<Move, Move?>> {
        var remaining: List<Move>? = this
        return generateSequence {
            val move = remaining?.firstOrNull()
            if (move != null) {
                val response = remaining?.drop(1)?.firstOrNull()
                remaining = remaining?.drop(2)
                Pair(move, response)
            } else null
        }
    }
}
