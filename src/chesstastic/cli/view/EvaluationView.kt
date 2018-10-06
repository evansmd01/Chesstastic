package chesstastic.cli.view

import chesstastic.ai.models.*
import chesstastic.engine.entities.Color
import chesstastic.util.*

object EvaluationView: TableView() {

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
            val baseScore = it.baseScore.format()
            val imbalance = it.baseScore.imbalance.format()
            val weight = "%.2f".format(it.weight)
            val total = it.weightedScore.format()

            indent + title.padded(32) +
                separator + baseScore.padded(19) +
                separator + imbalance.padded(11) +
                separator + weight.padded(8) +
                separator + total
        }
    }
}

