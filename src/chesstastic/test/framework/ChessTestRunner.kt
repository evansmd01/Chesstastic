package chesstastic.test.framework

import chesstastic.util.*

object ChessTestRunner {
    fun execute() {
        println()
        println("Executing tests...")

        var totalSuccess = 0
        var totalFail = 0

        val totalTime = Stopwatch.timeAction {
            TestReflection.testSuiteFactories.forEach { suiteFactory ->
                val (suite, duration) = Stopwatch.timeFunction(suiteFactory)
                println("Finished: ${suite.javaClass.kotlin.simpleName}")
                println("Execution time: ${duration.format()}")
                val success = suite.totalSuccessCount()
                val failure = suite.totalFailCount()
                if (success > 0) println("$success tests Succeeded")
                if (failure > 0) printlnRed("$failure tests failed")
                totalSuccess += success
                totalFail += failure
                println()
            }
        }

        println("FINISHED ALL TESTS")
        println("Total execution time: ${totalTime.format()}")
        if (totalSuccess > 0) println("$totalSuccess tests Succeeded")
        if (totalFail > 0) printlnRed("$totalFail tests failed")
    }
}
