package chesstastic.cli.view

import chesstastic.ai.heuristics.PositionEvaluation
import chesstastic.ai.heuristics.Score
import chesstastic.util.ConsoleColor
import chesstastic.util.times

object EvaluationView {
    private val indent = "     "

    private fun String.padded(toFit: Int) = this +  (toFit - this.length).times(" ")
    private val separator = "|  "

    private fun Score.format(): String =
        "${ConsoleColor.CYAN}%.2f l${ConsoleColor.RESET}  --  ${ConsoleColor.PURPLE}%.2f d${ConsoleColor.RESET}"
            .format(light, dark)

    fun render(evaluation: PositionEvaluation): String {
        return evaluation.heuristics.joinToString(
            prefix = indent + "HEURISTIC NAME".padded(30)
                + separator + "    BASE SCORE".padded(20)
                + separator + "MODIFIER".padded(10)
                + separator + "      TOTAL\n  ${98.times("-")}\n",
            separator = "\n"
        ) {

            val title = it.key.toString().toLowerCase().replace("_", " ").capitalize()
            val baseScore = it.baseScore.format()
            val modifier = " %.2f".format(it.modifier)
            val total = it.effectiveScore.format()

            indent + title.padded(30) +
                separator + baseScore.padded(38) +
                separator + modifier.padded(10) +
                separator + total
        }
    }
}
