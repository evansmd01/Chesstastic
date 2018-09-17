package chesstastic.cli

import chesstastic.cli.commands.*
import chesstastic.engine.entities.*
import chesstastic.cli.view.*
import chesstastic.engine.calculators.BoardCalculator
import chesstastic.test.ChessTests

fun main(args: Array<String>) {
    var board = Board.createNew()
    var validateMoves = true
    gameLoop@ while (true) {
        println()
        println(BoardView.render(board))
        if (board.isCheckmate) {
            printlnColor(ConsoleColor.YELLOW, "CHECKMATE!")
            printlnColor(ConsoleColor.YELLOW, "Congratulations ${board.turn.opposite} Player!")
            println(board.history.joinToString(separator = ","))
            break@gameLoop
        }
        if (board.isStalemate) {
            printlnColor(ConsoleColor.YELLOW, "Stalemate. It's a draw.")
            break@gameLoop
        }
        if (board.isCheck) {
            printlnColor(ConsoleColor.RED, "CHECK!")
        }
        print("${board.turn} player's turn: ")
        val input = readLine()?.toLowerCase()?.trim()
        val command = input?.let { Command.parse(it) }
        when (command) {
            is Command.Exit -> break@gameLoop
            is Command.Export -> {
                println(board.history.joinToString(separator = ","))
                break@gameLoop
            }
            is Command.Test -> {
                ChessTests.run()
                break@gameLoop
            }
            is Command.Load -> {
                board = Board.parse(command.history)
            }
            is Command.ShowMoves -> {
                println(BoardCalculator.legalMoves(board).toString())
            }
            is Command.DisableMoveValidation -> {
                validateMoves = false
            }
            is Command.Move -> {
                val move = if (validateMoves) {
                    BoardCalculator.legalMoves(board).firstOrNull {
                        it.from == command.from && it.to == command.to
                    }
                } else {
                    Move.Basic(command.from, command.to)
                }
                if (move != null)
                    if (move is chesstastic.engine.entities.Move.Promotion) {
                        promoteLoop@while (true) {
                            printlnColor(ConsoleColor.YELLOW, "Choose a Promotion! Enter 'Q' or 'K'")
                            val entry = readLine()?.toUpperCase()?.trim()
                            when (entry) {
                                "Q" -> { board = board.updated(move.withQueen); break@promoteLoop }
                                "K" -> { board = board.updated(move.withKnight); break@promoteLoop }
                            }
                        }
                    } else {
                        board = board.updated(move)
                    }
                else
                    printlnRed("Invalid move: $input")
            }
            else -> printlnRed("Invalid command: $input")
        }
    }
}

fun printlnRed(message: String) = printlnColor(ConsoleColor.RED, message)

fun printlnColor(colorEncoding: String, message: String) = println("$colorEncoding$message${ConsoleColor.RESET}")

fun printlnGreen(message: String) = printlnColor(ConsoleColor.GREEN, message)
