package chesstastic.ai.models

import chesstastic.engine.entities.Color

data class Score(val light: Double, val dark: Double) {

    operator fun plus(other: Score): Score = Score(other.light + light, other.dark + dark)

    operator fun times(quantity: Double): Score = Score(light * quantity, dark * quantity)

    operator fun div(denominator: Double): Score = Score(light / denominator, dark / denominator)

    fun ratioInFavorOf(color: Color): Double =
        when {
            light == dark -> 1.0
            color == Color.Light -> light / dark
            else -> dark / light
        }

    fun favors(color: Color): Boolean = when (color) {
        Color.Light -> light > dark
        Color.Dark -> dark > light
    }

    companion object {
        val EVEN = Score(0.0, 0.0)

        fun checkmate(winner: Color) = when (winner) {
            Color.Light -> Score(Double.POSITIVE_INFINITY, 0.0)
            Color.Dark -> Score(0.0, Double.POSITIVE_INFINITY)
        }
    }
}

