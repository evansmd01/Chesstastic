package chesstastic.ai.heuristics

import chesstastic.engine.entities.Color
import javafx.scene.effect.Light

data class Score(val light: Double, val dark: Double) {
    constructor(light: Int, dark: Int) : this(light.toDouble(), dark.toDouble())

    operator fun plus(other: Score): Score = Score(other.light + light, other.dark + dark)

    operator fun times(quantity: Double): Score = Score(light * quantity, dark * quantity)

    operator fun div(denominator: Double): Score = Score(light / denominator, dark / denominator)

    fun ratioInFavorOf(color: Color): Double =
        when {
            this == even -> 1.0
            color == Color.Light -> light / dark
            else -> dark / light
        }

    fun favors(color: Color): Boolean = when (color) {
        Color.Light -> light > dark
        Color.Dark -> dark > light
    }

    companion object {
        val even = Score(0.0, 0.0)

        fun checkmate(winner: Color) = when (winner) {
            Color.Light -> Score(Double.POSITIVE_INFINITY, 0.0)
            Color.Dark -> Score(0.0, Double.POSITIVE_INFINITY)
        }

        fun fromImbalance(light: Double, dark: Double): Score {
            val min = Math.max(light, dark)
            val diff = Math.abs(light - dark)
            val imbalance = diff / min
            return when  {
                light > dark -> Score(imbalance, 0.0)
                dark > light -> Score(0.0, imbalance)
                else -> even
            }
        }
    }
}
