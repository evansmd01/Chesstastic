package chesstastic.testing.framework

import chesstastic.engine.entities.*
import chesstastic.util.*

abstract class ChessTestSuite: ChessTestContext(parent = null), AssertionHelpers

open class ChessTestContext(val parent: ChessTestContext?) {
    private val children = mutableListOf<ChessTestContext>()
    private var successCount = 0
    fun totalSuccessCount(): Int = successCount + children.sumBy { it.totalSuccessCount() }
    private var failCount = 0
    fun totalFailCount(): Int = failCount + children.sumBy { it.totalFailCount() }
    private var skipCount = 0
    fun totalSkipCount(): Int = skipCount + children.sumBy { it.totalSkipCount() }

    private val indent: String by lazy {
        if(parent != null)
            parent.indent + "  "
        else " "
    }

    protected val tasks: MutableList<() -> Unit> = mutableListOf()
    fun taskCount():Int = tasks.count() + children.sumBy { it.taskCount() }

    protected val focusedTasks: MutableList<() -> Unit> = mutableListOf()
    fun hasFocus() = focusedTasks.any()

    fun execute() {
        if (focusedTasks.any()) {
            focusedTasks.forEach { it.invoke() }
        } else {
            tasks.forEach { it.invoke() }
        }
    }

    fun it(scenario: String, focus: Boolean = false, skip: Boolean = false, apply: () -> Unit) {
        if (skip) {
            tasks.add {
                skipCount++
                printlnYellow("$indent- [SKIPPING] $scenario")
            }
        } else {
            (if (focus) focusedTasks else tasks).add {
                try {
                    apply.invoke()
                    successCount++
                    printlnGreen("$indent- $scenario")
                } catch (error: Error) {
                    failCount++
                    printlnRed("$indent- it $scenario")
                    printlnRed("$indent   TEST FAILED: ${error.message}")
                    printlnRed("$indent   " + error.stackTrace.joinToString(separator = "\n$indent   ") { it.toString() })
                }
            }
        }
    }

    fun describe(descriptor: String, focus: Boolean = false, skip: Boolean = false, apply: ChessTestContext.() -> Unit) {
        val childContext = ChessTestContext(this)
        childContext.apply()
        children.add(childContext)

        if (skip) {
            tasks.add {
                skipCount += childContext.taskCount()
                printlnYellow("$indent- [SKIPPING] $descriptor")
            }
        } else {
            (if (focus || childContext.hasFocus()) focusedTasks else tasks).add {
                printlnGreen("$indent- $descriptor")
                childContext.execute()
            }
        }
    }
}

interface AssertionHelpers {
    fun <T> T.shouldBe(expected: T) {
        if (this != expected) throw AssertionError("$this did not equal $expected")
    }

    fun <T> Collection<T>.shouldBe(expected: Collection<T>) {
        if (this != expected) throw AssertionError("$this did not equal $expected")
    }

    fun <T> Collection<T>.shouldBeEquivalentTo(expected: Collection<T>) {
        if (this.count() != expected.count()) throw AssertionError("$this was not equivalent to $expected")
        this.shouldContainAll(expected)
    }

    fun <T> Collection<T>.shouldContainAll(expected: Collection<T>) {
        if (!expected.all{ this.contains(it) }) throw AssertionError("$this did not contain all of $expected")
    }

    fun <T> Collection<T>.shouldContain(expected: T) {
        if (!this.contains(expected)) throw AssertionError("$this did not contain $expected")
    }

    fun <T> Collection<T>.shouldNotContain(unexpected: T) {
        if(this.contains(unexpected)) throw AssertionError("$this contained $unexpected")
    }

    fun <T> Collection<T>.shouldNotContain(unexpected: (T) -> Boolean) {
        if(this.any(unexpected)) throw AssertionError("$this contained $unexpected")
    }

    fun Board.shouldMatch(snapshot: String) {
        val other = Snapshot.parse(snapshot, this.historyMetadata.currentTurn)
        val positionEqual = this.positionEquals(other)
        if(!positionEqual) throw AssertionError("\nBoard with state:\n\n${Snapshot.from(this)}\n\ndid not equal:\n\n${Snapshot.from(other)}\n".prependIndent("       "))
    }
}
