package chesstastic.util

import java.time.Duration

fun <T> T.transformIf(predicate: (T) -> Boolean, map: (T) -> T) = if(predicate(this)) map(this) else this

fun Duration.format(): String {
    val totalMillis = this.toMillis()
    val totalSeconds = totalMillis / 1000
    val totalMinutes = totalSeconds / 60

    return "${totalMinutes.format(2)}:${(totalSeconds % 60).format(2)}.${(totalMillis % 1000).format(3)}"
}

fun Long.format(digits: Int): String = String.format("%0${digits}d", this)
