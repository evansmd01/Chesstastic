package chesstastic.cli

import chesstastic.cli.commands.Command
import chesstastic.cli.commands.Exit
import chesstastic.cli.commands.Move
import chesstastic.engine.Board
import chesstastic.ui.cli.commands.*
import chesstastic.cli.view.BoardView
import chestastic.engine.*

fun main(args: Array<String>) {
    var board = Board()
    gameLoop@ while (true) {
        println(BoardView.render(board))
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



