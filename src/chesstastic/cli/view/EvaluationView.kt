package chesstastic.cli.view

import chesstastic.ai.heuristics.models.PositionEvaluation
import chesstastic.ai.heuristics.models.Score
import chesstastic.util.*

object EvaluationView {
    private val indent = "   "
    private val separator = "|  "

    private fun Score.format(): String =
        "${ConsoleColor.CYAN}%.2f l${ConsoleColor.RESET}  --  ${ConsoleColor.PURPLE}%.2f d${ConsoleColor.RESET}"
            .format(light, dark)

    fun render(evaluation: PositionEvaluation): String {
        return evaluation.heuristics.joinToString(
            prefix = indent + "HEURISTIC NAME".padded(32)
                + separator + "    BASE SCORE".padded(20)
                + separator + "MODIFIER".padded(10)
                + separator + "      TOTAL\n${100.times("-")}\n",
            separator = "\n",
            postfix = "\n${100.times("-")}\n\n" + ScoreView.render(evaluation.finalScore)
        ) {
            val title = it.key.toString().toLowerCase().replace("_", " ").capitalize()
            val baseScore = it.imbalance.score.format()
            val modifier = " %.2f".format(it.weight)
            val total = it.weightedScore.format()

            indent + title.padded(32) +
                separator + baseScore.padded(20) +
                separator + modifier.padded(10) +
                separator + total
        }
    }
}
