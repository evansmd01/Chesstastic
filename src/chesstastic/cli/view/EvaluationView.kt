package chesstastic.cli.view

import chesstastic.ai.models.*
import chesstastic.engine.entities.Color
import chesstastic.util.*

object EvaluationView: TableView() {

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

