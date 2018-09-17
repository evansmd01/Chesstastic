package chesstastic.cli.view

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Color.*

class BoardView {
    companion object {
        fun render(board: Board): String {
            val sb = StringBuilder()
            val fileLabels = File.values().joinToString(separator = "           ", prefix = "        ")
            sb.appendln(fileLabels)
            sb.appendln()
            Rank.values().reversed().forEach { rank ->
                val squareViews = File.values().map { file ->
                    val bgColor = if ((rank.index + file.index) % 2 == 0) Light else Dark
                    val piece = board[Square(file, rank)]
                    SquareView.render(bgColor, piece)
                }
                // line by line, scroll through each squareView and append the corresponding line
                (0..5).forEach { lineIndex ->
                    val rankLabel = if(lineIndex == 3) "${rank.index + 1}  " else "   "
                    sb.append(rankLabel)
                    squareViews.forEach {
                        it[lineIndex].forEach { sb.append(it) }
                    }
                    sb.appendln("  $rankLabel")
                }
            }
            sb.appendln()
            sb.appendln(fileLabels)
            return sb.toString()
        }
    }
}

