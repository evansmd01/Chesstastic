package chesstastic.cli

import chesstastic.cli.commands.*
import chesstastic.engine.entities.*
import chesstastic.cli.view.*
import chesstastic.engine.rules.MoveCalculator

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
        if (board.isCheck) {
            printlnColor(ConsoleColor.RED, "CHECK!")
        }
        print("${board.turn} player's turn: ")
        val input = readLine()?.toLowerCase()?.trim()
        val command = input?.let { Command.parse(it) }
        when (command) {
            is ExitCommand -> break@gameLoop
            is PrintCommand -> println(command.print(board))
            is MoveCommand -> {
                val move = MoveCalculator.legalMoves(board).firstOrNull{
                    it.from == command.from && it.to == command.to
                }
                if (move != null)
                    if (move is PawnPromotionMove) {
                        promoteLoop@while (true) {
                            printlnColor(ConsoleColor.YELLOW, "Choose a Promotion! Enter 'Q' or 'K'")
                            val entry = readLine()?.toUpperCase()?.trim()
                            when (entry) {
                                "Q" -> { board = board.update(move.withQueen); break@promoteLoop }
                                "K" -> { board = board.update(move.withKnight); break@promoteLoop }
                            }
                        }
                    } else {
                        board = board.update(move)
                    }
                else
                    printlnError("Invalid move: $input")
            }
            else -> printlnError("Invalid command: $input")
        }
    }
}

fun printlnError(message: String) = printlnColor(ConsoleColor.RED, message)

fun printlnColor(colorEncoding: String, message: String) = println("$colorEncoding$message${ConsoleColor.RESET}")
