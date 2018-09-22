package chesstastic.util

import java.time.Duration

object Stopwatch {
    fun <T> timeFunction(function: () -> T): Pair<T, Duration> {
        val startTime = System.currentTimeMillis()
        val retVal = function()
        return retVal to Duration.ofMillis(System.currentTimeMillis() - startTime)
    }

    fun timeAction(task: () -> Unit): Duration = timeFunction(task).second
}
