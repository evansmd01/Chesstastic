package chesstastic.cli.view

import chesstastic.util.padded
import chesstastic.util.practicalLength
import java.lang.StringBuilder

object ColumnsView {
    fun render(column1: String, column2: String): String {
        val sb = StringBuilder()
        val col1Lines = column1.lines()
        val col2Lines = column2.lines()
        val column1Width = column1.lines().maxBy { it.practicalLength() }?.practicalLength() ?: 0
        val maxLines = Math.max(col1Lines.size, col2Lines.size)

        for(lineIndex in 0 until maxLines) {
            val col1Line = col1Lines.elementAtOrElse(lineIndex) { "" }
            val col2Line = col2Lines.elementAtOrElse(lineIndex) { "" }
            val line = col1Line.padded(column1Width) + " | " + col2Line
            sb.appendln(line)
        }
        return sb.toString()
    }
}
