package chesstastic.cli

import chesstastic.cli.commands.*
import chesstastic.engine.entities.*
import chesstastic.cli.view.*

fun main(args: Array<String>) {
    var board = Board.createNew()
    gameLoop@ while (true) {
        println()
        println(BoardView.render(board))
        if (board.isCheckmate) {
            printlnColor(ConsoleColor.YELLOW, "CHECKMATE!")
            printlnColor(ConsoleColor.YELLOW, "Congratulations ${board.turn.opposite} Player!")
            break@gameLoop
        }
        print("${board.turn} player's turn: ")
        val input = readLine()?.toLowerCase()?.trim()
        val command = input?.let { Command.parse(it) }
        when (command) {
            is ExitCommand -> break@gameLoop
            is MoveCommand -> {
                val newBoard = board.update(Move(command.from, command.to))
                if (newBoard != null)
                    board = newBoard
                else
                    printlnError("Invalid move: $input")
            }
            else -> printlnError("Invalid command: $input")
        }
    }
}

fun printlnError(message: String) = printlnColor(ConsoleColor.RED, message)

fun printlnColor(colorEncoding: String, message: String) = println("$colorEncoding$message${ConsoleColor.RESET}")
