package chesstastic.cli.view

import chesstastic.ai.heuristics.Score
import chesstastic.engine.entities.Color
import chesstastic.util.ConsoleColor

object ScoreView {
    private const val BAR_SIZE = 80

    fun render(score: Score): String {
        val (lightSegments, darkSegments) = segments(score)
        val lightBar = (1..lightSegments).joinToString(
            separator = "",
            prefix = ConsoleColor.CYAN,
            postfix = ConsoleColor.RESET
        ) { "x" }
        val darkBar = (1..darkSegments).joinToString(
            separator = "",
            prefix = ConsoleColor.PURPLE,
            postfix = ConsoleColor.RESET
        ) { "x" }

        val lightScore = if (score.favors(Color.Light)) score.ratioInFavorOf(Color.Light) else 1.0
        val darkScore = if(score.favors(Color.Dark)) score.ratioInFavorOf(Color.Light) else 1.0

        return "    %.2f - $lightBar$darkBar - %.2f".format(lightScore, darkScore)
    }

    private fun segments(score: Score): Pair<Int, Int> =
        when {
            score.light == Double.POSITIVE_INFINITY -> Pair(BAR_SIZE - 1, 1)
            score.dark == Double.POSITIVE_INFINITY -> Pair(1, BAR_SIZE - 1)
            else -> {
                val ratio = score.ratioInFavorOf(Color.Light)
                val segmentSize = BAR_SIZE / (ratio + 1)
                val lightSegments = Math.max(1, Math.min((ratio * segmentSize).toInt(), BAR_SIZE - 1))
                val darkSegments = BAR_SIZE - lightSegments
                Pair(lightSegments, darkSegments)
            }
        }
}
