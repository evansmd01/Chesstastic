package chesstastic.cli

import chesstastic.cli.commands.*
import chesstastic.engine.Board
import chesstastic.cli.view.BoardView

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
        }
    }
}



