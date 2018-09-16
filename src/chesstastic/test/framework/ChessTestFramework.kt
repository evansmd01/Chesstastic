package chesstastic.test.framework

import chesstastic.cli.printlnGreen
import chesstastic.cli.printlnRed
import chesstastic.test.engine.entities.*

class ChessTestFramework {
    companion object {
        fun execute(suiteFactories: List<() -> ChessTestSuite>) {
            println()
            println("Executing tests...")

            var totalSuccess = 0
            var totalFail = 0

            suiteFactories.forEach { suiteFactory ->
                val suite = suiteFactory()
                println("Finished: ${suite.javaClass.kotlin.simpleName}")
                val success = suite.totalSuccessCount()
                val failure = suite.totalFailCount()
                if (success > 0) println("$success tests Succeeded")
                if (failure > 0) printlnRed("$failure tests failed")
                totalSuccess += success
                totalFail += failure
                println()
            }

            println("FINISHED ALL TESTS")
            if (totalSuccess > 0) println("$totalSuccess tests Succeeded")
            if (totalFail > 0) printlnRed("$totalFail tests failed")
        }
    }
}

abstract class ChessTestSuite: ChessTestContext(parent = null), AssertionHelpers {
    init {
        println("Running: ${javaClass.kotlin.simpleName}")
    }
}

open class ChessTestContext(val parent: ChessTestContext?) {
    private val children = mutableListOf<ChessTestContext>()
    private var successCount = 0
    fun totalSuccessCount(): Int = successCount + children.sumBy { it.totalSuccessCount() }
    private var failCount = 0
    fun totalFailCount(): Int = failCount + children.sumBy { it.totalFailCount() }

    private val indent: String by lazy {
        if(parent != null)
            parent.indent + "  "
        else " "
    }

    fun it(scenario: String, apply: () -> Unit) {
        try {
            apply.invoke()
            successCount++
            printlnGreen("$indent- $scenario")
        }
        catch(error: Error) {
            failCount++
            printlnRed("$indent- it $scenario")
            printlnRed("$indent   TEST FAILED: ${error.message}")
            printlnRed("$indent   " + error.stackTrace.joinToString(separator = "\n$indent   ") { it.toString() })
        }
    }

    fun describe(descriptor: String, apply: ChessTestContext.() -> Unit) {
        printlnGreen("$indent- $descriptor")
        val context = ChessTestContext(this)
        children.add(context)
        context.apply()
    }
}

interface AssertionHelpers {
    fun <T> T.shouldBe(expected: T) {
        if (this != expected) throw AssertionError("$this did not equal $expected")
    }

    fun <T> T.shouldNotBe(expected: T) {
        if (this == expected) throw AssertionError("$this was equal to $expected")
    }

    fun <T> Iterable<T>.shouldBe(expected: Iterable<T>) {
        if (this != expected) throw AssertionError("$this did not equal $expected")
    }

    fun <T> Iterable<T>.shouldBeEquivalent(expected: Iterable<T>) {
        if (this.count() != expected.count()) throw AssertionError("$this was not equivalent to $expected")
        if (!this.all { expected.contains(it) }) throw AssertionError("$this was not equivalent to $expected")
    }
}
