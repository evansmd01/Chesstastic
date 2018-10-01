package chesstastic.ai.heuristics.models

import kotlin.math.*


/**
 * Represents the imbalance between two measurements for light and dark.
 * Filters out the "background noise" of the portion of the measurements that match,
 * in order to make the imbalance more apparent, and to avoid awarding points for evenly matched measurements.
 *
 * For example, let's say we are measuring material on the board. At the start of the game, both sides are even:
 *  8 pawns * 1 + 2 rooks * 5 + 2 knights * 3 + 2 bishops * 3 + 1 queen * 9 = 39
 *
 *  Material
 *  given l = 39 & d = 39, score = 39 to 39 for Material
 *
 *  Later, when we give one side a point for having some control of the center
 *  occupying a center square = 1 pt, attacking = 0.5.
 *  Light moves e2e4, attacking 1 and occupying 1 = 1.5 pts
 *  Dark response g8f6, attacking 2 and occupying 0 = 1 pts
 *
 *  Central Control
 *  given l = 1.5 & d = 1, score = 1.5 to 1 for Center
 *
 *  given min base score = 1 to 1
 *  Total score = 1 + 39 + 1.5 to 1 + 39 + 1 = 41.5 to 41 = 1.012 l/d ratio
 *
 *  The score increase for occupying the center is minimal, and does not create much incentive to fight for control
 *
 *  Instead, we take the difference between scores, divided by the higher score to get
 *  the ratio of the difference relative to the magnitude of the measurements,
 *  then multiply that ratio time the original high measurement, and give those points to only the leader.
 *
 *  Imbalance Formula:
 *      abs(l - d) / max(l, d) * max(l, d)
 *
 *  Using the same examples above, we get:
 *
 *  Material
 *  given l = 39 & d = 39
 *  imbalance = abs(39 - 39) / min(39,39) * max(39,39) = 0
 *  score = 0 to 0, no score awarded to either side for being evenly matched
 *
 *  Central Control
 *  given l = 1.5 & d = 1
 *  imbalance = abs(1.5 - 1) / min(1.5, 1) * max(1.5, 1) = .5 / 1 * 1.5 = 0.75
 *  score = 0.75 to 0, imbalance awarded to leading side, no points to other side
 *
 *  given min base score = 1 to 1
 *  Total score = 1 + 0 + 0.75 to 1 + 0 + 0 = 1.75 to 1 = 1.75 l/d ratio
 *
 *
 *  given same absolute difference, but greater points on either side.
 *  For instance, if both sides have been fighting for control of center and have more points each
 *
 *  given l = 3.5 & d = 4.0
 *  note: the absolute difference is still 0.5, just as before, but 0.5 is a smaller ratio
 *  relative to the magnitude of the measurments, so the imbalance should be mitigated.
 *
 *  imbalance = abs(3.5 - 4.0) / min(3.5, 4.0) * max(3.5, 4.0) = 0.5 / 3.5 * 4.0 = 0.57
 *  score = 0 to 0.57, imbalance is smaller than before, still only awarded to leading side.
 */
data class Imbalance(val light: Double, val dark: Double) {
    init {
        if (light < 0 || dark < 0)
            throw Exception("Invalid imbalance. Cannot have values of less than zero. " +
                "Apply penalties as bonuses to the opponent")
    }

    val value = when {
        light == dark -> 0.0 // avoids divide by zero
        else -> abs(light - dark) / (light + dark) * max(light, dark)
    }

    val score = when {
        light > dark -> Score(value, 0.0)
        dark > light -> Score(0.0, value)
        else -> Score.EVEN
    }
}

