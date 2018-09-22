package chesstastic.test.framework

import chesstastic.cli.printlnGreen
import chesstastic.cli.printlnRed
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Snapshot
import java.time.Duration

class ChessTestFramework {
    companion object {
        fun execute(suiteFactories: List<() -> ChessTestSuite>) {
            println()
            println("Executing tests...")

            var totalSuccess = 0
            var totalFail = 0

            val totalTime = Stopwatch.timeAction {
                suiteFactories.forEach { suiteFactory ->
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

    fun <T> Iterable<T>.shouldBeEquivalentTo(expected: Iterable<T>) {
        if (this.count() != expected.count()) throw AssertionError("$this was not equivalent to $expected")
        this.shouldContainAll(expected)
    }

    fun <T> Iterable<T>.shouldContainAll(expected: Iterable<T>) {
        if (!expected.all{ this.contains(it) }) throw AssertionError("$this did not contain all of $expected")
    }

    fun <T> Iterable<T>.shouldContain(expected: T) {
        if (!this.contains(expected)) throw AssertionError("$this did not contain $expected")
    }

    fun <T> Iterable<T>.shouldNotContain(unexpected: (T) -> Boolean) {
        if(this.any(unexpected)) throw AssertionError("$this contained $unexpected")
    }

    fun Board.shouldMatch(snapshot: String) {
        val other = Snapshot.parse(snapshot, this.turn)
        val positionEqual = this.positionEquals(other)
        if(!positionEqual) throw AssertionError("\nBoard with state:\n\n${Snapshot.from(this)}\n\ndid not equal:\n\n${Snapshot.from(other)}\n".prependIndent("       "))
    }
}

object Stopwatch {
    fun <T> timeFunction(function: () -> T): Pair<T, Duration> {
        val startTime = System.currentTimeMillis()
        val retVal = function()
        return retVal to Duration.ofMillis(System.currentTimeMillis() - startTime)
    }

    fun timeAction(task: () -> Unit): Duration = timeFunction(task).second
}

fun Duration.format(): String {
    val totalMillis = this.toMillis()
    val totalSeconds = totalMillis / 1000
    val totalMinutes = totalSeconds / 60

    return "${totalMinutes.format(2)}:${(totalSeconds % 60).format(2)}.${(totalMillis % 1000).format(3)}"
}

fun Long.format(digits: Int): String = String.format("%0${digits}d", this)
