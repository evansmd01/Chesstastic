package chesstastic.UI.cli

import chesstastic.UI.cli.commands.*
import chesstastic.UI.cli.view.BoardView
import chestastic.Engine.*

fun main(args: Array<String>) {
    var board = Board()
    gameLoop@ while (true) {
        print(BoardView.render(board))
        print("Enter Command: ")
        val input = readLine()?.toLowerCase()?.trim()
        val command = input?.let { Command.parse(it) }
        when (command) {
            is Exit -> break@gameLoop
            is Move -> board = board.move(command.from, command.to)
            else -> continue@gameLoop
        }
    }
}



