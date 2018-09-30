package chesstastic.tests.framework

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
        if (parent != null)
            parent.indent + "  "
        else " "
    }

    protected val tasks: MutableList<() -> Unit> = mutableListOf()
    fun taskCount(): Int = tasks.count() + children.sumBy { it.taskCount() }

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
