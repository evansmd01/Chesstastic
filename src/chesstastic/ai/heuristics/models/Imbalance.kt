package chesstastic.ai.heuristics.models

data class Imbalance(val light: Double, val dark: Double) {
    init {
        if (light < 0 || dark < 0)
            throw Exception("Invalid imbalance. Cannot have values of less than zero. " +
                "Apply penalties as bonuses to the opponent")
    }

    val min = Math.max(light, dark) + 1 // + 1 ensures no infinity calculations or exponential divisions by decimals
    val diff = Math.abs(light - dark)
    val imbalance = diff / (min)
    val score = when  {
        light > dark -> Score(imbalance, 0.0)
        dark > light -> Score(0.0, imbalance)
        else -> Score.EVEN
    }
}
