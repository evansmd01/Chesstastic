package chesstastic.cli.view

import chesstastic.cli.view.messages.MessageView
import chesstastic.engine.entities.*
import chesstastic.engine.entities.Color.*

// import e2e4 f7f5 d1h5

object BoardView {
    fun render(board: Board): String {
        val messageView = MessageView.get(board)
        val messageColumnRange = messageView.columnRange(8 * SquareView.SQUARE_WIDTH)
        val messageLineRange = messageView.lineRange(8 * SquareView.SQUARE_HEIGHT)
        val message = messageView.render()

        val sb = StringBuilder()
        val fileLabels = File.values().joinToString(separator = "           ", prefix = "        ")
        sb.appendln(fileLabels)
        sb.appendln()
        Rank.values().reversed().forEachIndexed { reversedRankIndex, rank ->
            val squareViews = File.values().map { file ->
                val bgColor = if ((rank.index + file.index) % 2 == 0) Light else Dark
                val piece = board[Square(file, rank)]
                SquareView.render(bgColor, piece)
            }
            // line by line, scroll through each squareView and append the corresponding line
            (0..(SquareView.SQUARE_HEIGHT - 1)).forEach { lineIndex ->
                val rankLabel = if (lineIndex == 3) "${rank.index + 1}  " else "   "
                sb.append(rankLabel)

                val totalLineIndex = (reversedRankIndex * SquareView.SQUARE_HEIGHT) + lineIndex

                squareViews.forEachIndexed { squareIndex, squareView ->
                    squareView[lineIndex].forEachIndexed { squareColumnIndex, charString ->
                        val totalColumnIndex = (squareIndex * SquareView.SQUARE_WIDTH) + squareColumnIndex
                        if(totalLineIndex in messageLineRange && totalColumnIndex in messageColumnRange) {
                            val msgLine = totalLineIndex - messageLineRange.first
                            val msgCol = totalColumnIndex - messageColumnRange.first
                            val msgChar = message[msgLine][msgCol]
                            if (msgChar == " ") sb.append(charString) else sb.append(msgChar)
                        } else {
                            sb.append(charString)
                        }
                    }
                }

                sb.appendln("  $rankLabel")
            }
        }
        sb.appendln()
        sb.appendln(fileLabels)
        return sb.toString()
    }
}

