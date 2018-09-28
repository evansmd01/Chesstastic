package chesstastic.testing

import chesstastic.testing.framework.TestReflection
import chesstastic.util.*

fun main(args: Array<String>) {
    ChessTestRunner.execute()
}

object ChessTestRunner {
    fun execute() {
        println("Executing tests...")

        var totalSuccess = 0
        var totalFail = 0
        var totalSkip = 0

        val totalTime = Stopwatch.timeAction {
            TestReflection.testSuiteFactories.forEach { suiteFactory ->
                val (suite, duration) = Stopwatch.timeFunction(suiteFactory)
                println("Finished: ${suite.javaClass.kotlin.simpleName}")
                println("Execution time: ${duration.format()}")
                val success = suite.totalSuccessCount()
                val failure = suite.totalFailCount()
                val skip = suite.totalSkipCount()
                if (success > 0) println("$success tests Succeeded")
                if (failure > 0) printlnRed("$failure tests failed")
                if (skip > 0) printlnYellow("$skip tests skipped")
                totalSuccess += success
                totalFail += failure
                totalSkip += skip
                println()
            }
        }

        println("FINISHED ALL TESTS")
        println("Total execution time: ${totalTime.format()}")
        if (totalSuccess > 0) println("$totalSuccess tests Succeeded")
        if (totalFail > 0) printlnRed("$totalFail tests failed")
        if (totalSkip > 0) printlnYellow("$totalSkip tests skipped")
    }
}
