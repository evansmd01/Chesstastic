package chesstastic.cli.view.messages

import chesstastic.engine.entities.Board
import chesstastic.util.ConsoleColor

interface MessageView {
    val drawing: String

    fun lines() = drawing.lines()
        .map { it.trim().replace("|", "") }
        .filterNot { it.isEmpty() }

    fun width() = lines().map { it.length }.max() ?: 0

    fun columnRange(nColumns: Int): IntRange {
        val w = width()
        val start = (nColumns - w) / 2
        return start..(start+w-1)
    }
    fun lineRange(nLines: Int): IntRange {
        val h = lines().count()
        val start = (nLines - h + 1) / 2
        return start..(start+h-1)
    }

    fun render(): List<List<String>> =
        lines().map { line ->
                line.map { c -> c.toString() }
                    .map { if(it != " ") "${ConsoleColor.YELLOW}$it${ConsoleColor.RESET}" else it }
            }

    companion object {
        fun get(board: Board): MessageView = when {
            board.isCheckmate -> CheckmateView
            board.isStalemate -> StalemateView
            board.isCheck -> CheckView
            else -> EmptyMessage
        }
    }
}

object EmptyMessage: MessageView {
    override val drawing: String = ""

    override fun columnRange(nColumns: Int): IntRange = -1..-1
    override fun lineRange(nLines: Int): IntRange = -1..-1
}


