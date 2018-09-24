package chesstastic.testing.framework

import chesstastic.engine.entities.*
import chesstastic.util.*

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
        val other = Snapshot.parse(snapshot, this.historyMetadata.currentTurn)
        val positionEqual = this.positionEquals(other)
        if(!positionEqual) throw AssertionError("\nBoard with state:\n\n${Snapshot.from(this)}\n\ndid not equal:\n\n${Snapshot.from(other)}\n".prependIndent("       "))
    }
}
