package chesstastic.performance

import chesstastic.util.div
import chesstastic.util.format
import chesstastic.util.printlnCyan
import chesstastic.util.printlnYellow
import java.time.Duration
import java.time.LocalDateTime

object PerformanceLog {
    fun log(description: String, duration: Duration, positionsEvaluated: Int) {
        val file = java.io.File("${System.getProperty("user.dir")}/data/performance/performance-test-log.csv")
        file.appendText("\"$description\",${LocalDateTime.now()},${duration.format()},$positionsEvaluated,${(duration / positionsEvaluated).format()}\n")
        val positionsPerSecond = (positionsEvaluated * 1000) / duration.toMillis()
        printlnCyan("Processed $positionsEvaluated in ${duration.format()}, or $positionsPerSecond / second")
    }
}
