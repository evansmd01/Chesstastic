package chesstastic.ai.heuristics

import chesstastic.engine.entities.Color

data class Score(val light: Double, val dark: Double) {
    constructor(light: Int, dark: Int) : this(light.toDouble(), dark.toDouble())

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
        fun forOnly(color: Color, score: Double) = when (color) {
            Color.Light -> Score(score, 0.0)
            Color.Dark -> Score(0.0, score)
        }

        fun safeDivide(numerator: Double, denominator: Double): Double {
            var n = numerator
            var d = denominator
            if(denominator == 0.0) {
                val inc = 0.00001
                n += inc
                d += inc
            }
            return n/d
        }
    }
}