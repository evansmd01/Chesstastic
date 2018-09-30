package chesstastic.cli.view

import chesstastic.util.padded
import chesstastic.util.practicalLength
import java.lang.StringBuilder

object ColumnsView {
    fun render(left: String, right: String): String {
        val sb = StringBuilder()
        val leftLines = left.lines()
        val rightLines = right.lines()
        val leftSideWidth = left.lines().maxBy { it.practicalLength() }?.practicalLength() ?: 0
        val maxLines = Math.max(leftLines.size, rightLines.size)
        for(lineIndex in 0 until maxLines) {
            val leftLine = leftLines.elementAtOrElse(lineIndex) { "" }.padded(leftSideWidth)
            val rightLine = rightLines.elementAtOrElse(lineIndex) { "" }
            sb.appendln("$leftLine | $rightLine")
        }
        return sb.toString()
    }
}
