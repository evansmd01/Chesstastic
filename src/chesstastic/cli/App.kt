package chesstastic.cli

import chesstastic.ai.ChesstasticAI
import chesstastic.cli.commands.*
import chesstastic.engine.entities.*
import chesstastic.cli.view.*
import chesstastic.test.ChessTests

fun main(args: Array<String>) {
    var board = Board.createNew()
    var validateMoves = true
    var lightPlayer = Player.Human
    var darkPlayer = Player.Human
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
            println(board.history.joinToString(separator = ","))
            break@gameLoop
        }
        if (board.isCheck) {
            printlnColor(ConsoleColor.RED, "CHECK!")
        }
        print("${board.turn} player's turn: ")
        val player = if (board.turn == Color.Light) lightPlayer else darkPlayer
        when (player) {
            Player.AI -> {
                val move = if (board.turn == Color.Dark)
                    ChesstasticAI.selectMove(board, 4, 3)
                else
                    ChesstasticAI.selectMove(board, 1, 1)
                board = board.updated(move)
            }
            Player.Human -> {
                val input = readLine()?.toLowerCase()?.trim()
                val command = input?.let { Command.parse(it) }
                when (command) {
                    is Command.DisableMoveValidation -> {
                        validateMoves = false
                    }
                    is Command.Exit -> break@gameLoop
                    is Command.Export -> {
                        println(board.history.joinToString(separator = ","))
                        break@gameLoop
                    }
                    is Command.SetPlayer -> when (board.turn) {
                        Color.Light -> lightPlayer = command.player
                        Color.Dark -> darkPlayer = command.player
                    }
                    is Command.Test -> {
                        ChessTests.run()
                        break@gameLoop
                    }
                    is Command.Load -> {
                        board = Board.parse(command.history)
                    }
                    is Command.ShowMoves -> {
                        println(board.legalMoves.toString())
                    }

                    is Command.Move -> {
                        val move = if (validateMoves) {
                            board.legalMoves.firstOrNull {
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

    }
}

fun printlnRed(message: String) = printlnColor(ConsoleColor.RED, message)

fun printlnColor(colorEncoding: String, message: String) = println("$colorEncoding$message${ConsoleColor.RESET}")

fun printlnGreen(message: String) = printlnColor(ConsoleColor.GREEN, message)
