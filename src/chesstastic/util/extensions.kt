package chesstastic.util

import java.time.Duration

fun <T> T.transformIf(predicate: (T) -> Boolean, map: (T) -> T) = if(predicate(this)) map(this) else this

fun Duration.format(): String {
    val totalMillis = this.toMillis()
    val totalSeconds = totalMillis / 1000
    val totalMinutes = totalSeconds / 60

    return "${totalMinutes.format(2)}:${(totalSeconds % 60).format(2)}.${(totalMillis % 1000).format(3)}"
}

operator fun Duration.div(int: Int): Duration = Duration.ofMillis(this.toMillis() / int)

fun Long.format(digits: Int): String = String.format("%0${digits}d", this)

fun Int.times(string: String): String = (1..this).joinToString(separator = ""){ string }

fun String.padded(toFit: Int): String {
    return this + (toFit - this.practicalLength()).times(" ")
}

fun String.practicalLength(): Int = ConsoleColor.all.fold(this) { acc, color -> acc.replace(color, "")}.length
