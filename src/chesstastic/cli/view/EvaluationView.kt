package chesstastic.cli.view

import chesstastic.ai.heuristics.models.Imbalance
import chesstastic.ai.heuristics.models.PositionEvaluation
import chesstastic.ai.heuristics.models.Score
import chesstastic.engine.entities.Color
import chesstastic.util.*

object EvaluationView {
    private fun Double.format() = "%.2f".format(this)
    private val indent = "   "
    private val separator = "|  "

    private fun Score.format(): String {
        val lightString = light.format()
        val darkString = dark.format()
        return when {
            light > dark -> "${lightString.applyColor(Color.Light)} to $darkString"
            light < dark -> "$lightString to ${darkString.applyColor(Color.Dark)}"
            else -> "$lightString to $darkString"
        }
    }

    private fun Imbalance.format(): String {
        val lightString = if (light > 0) light.format().applyColor(Color.Light) else light.format()
        val darkString = if (dark > 0) dark.format().applyColor(Color.Dark) else dark.format()
        val imbalance = when {
            light > dark -> value.format().applyColor(Color.Light)
            light < dark -> value.format().applyColor(Color.Dark)
            else -> value.format()
        }.padded(11)
        val base = "$lightString to $darkString".padded(19)
        return "$base|  $imbalance"
    }

    fun render(evaluation: PositionEvaluation): String {
        return evaluation.heuristics.joinToString(
            prefix = indent + "HEURISTIC NAME".padded(32)
                + separator + "BASE MEASUREMENTS".padded(19)
                + separator + "IMBALANCE".padded(11)
                + separator + "WEIGHT".padded(8)
                + separator + "SCORE\n${100.times("-")}\n",
            separator = "\n",
            postfix = "\n${100.times("-")}\n\n" + ScoreView.render(evaluation.finalScore)
        ) {
            val title = it.key.toString().toLowerCase().replace("_", " ").capitalize()
            val imbalance = it.imbalance.format()
            val weight = "%.2f".format(it.weight)
            val total = it.weightedScore.format()

            indent + title.padded(32) +
                separator + imbalance.padded(20) +
                separator + weight.padded(8) +
                separator + total
        }
    }
}
