package chesstastic.performance

import chesstastic.util.div
import chesstastic.util.format
import java.time.Duration
import java.time.LocalDateTime

object PerformanceLog {
    fun log(description: String, duration: Duration, positionsEvaluated: Int) {
        val file = java.io.File("${System.getProperty("user.dir")}/data/performance/performance-test-log.csv")
        val line = "\"$description\",${LocalDateTime.now()},${duration.format()},$positionsEvaluated,${(duration / positionsEvaluated).format()}"
        print(line)
        file.appendText(line)
    }
}
