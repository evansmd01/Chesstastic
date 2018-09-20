package chesstastic.ai.values

import chesstastic.ai.criteria.safeDivide
import chesstastic.engine.entities.Color

data class Score(val light: Double, val dark: Double) {
    operator fun plus(other: Score): Score = Score(other.light + light, other.dark + dark)

    operator fun times(quantity: Double): Score = Score(light * quantity, dark * quantity)

    operator fun div(denominator: Double): Score = Score(light / denominator, dark / denominator)

    fun ratioInFavorOf(color: Color) =
        if (color == Color.Light)
            safeDivide(light, dark)
        else
            safeDivide(dark, light)

    companion object {
        val even = Score(0.0, 0.0)

        fun checkmate(winner: Color) =
            if (winner == Color.Light)
                Score(999999.0, 0.0)
            else
                Score(0.0, 999999.0)
    }
}