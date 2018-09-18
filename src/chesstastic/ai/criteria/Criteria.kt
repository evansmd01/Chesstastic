package chesstastic.ai.criteria

import chesstastic.ai.values.Score
import chesstastic.engine.entities.Board

interface Criteria {
    fun evaluate(board: Board): Score
}

fun safeDivide(numerator: Double, denominator: Double): Double {
    var n = numerator
    var d = denominator
    if(denominator == 0.0) {
        val inc = 0.00000001
        n += inc
        d += inc
    }
    return n/d
}
