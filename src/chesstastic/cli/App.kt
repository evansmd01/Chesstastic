package chesstastic.cli

import chesstastic.ai.ChesstasticAI
import chesstastic.cli.commands.Command
import chesstastic.cli.view.BoardView
import chesstastic.engine.entities.*
import chesstastic.test.ChessTests

fun main(args: Array<String>) {
    var board = Board.createNew()
    var validateMoves = true
    var skipPrint = false
    var lightAI: ChesstasticAI? = null
    var darkAI: ChesstasticAI? = null
    gameLoop@ while (true) {
        println()
        if (skipPrint) skipPrint = false
        else println(BoardView.render(board))

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
        val ai = if (board.turn == Color.Light) lightAI else darkAI
        when  {
            ai != null -> {
                board = board.updated(ai.selectMove(board))
            }
            else -> {
                val input = readLine()?.toLowerCase()?.trim()
                val command = input?.let { Command.parse(it) }
                when (command) {
                    is Command.DisableMoveValidation -> {
                        validateMoves = false
                    }
                    is Command.Exit -> break@gameLoop
                    is Command.Export -> {
                        println()
                        println("State:\n" + Snapshot.from(board))
                        println()
                        println("History:\n" + board.history.joinToString(separator = ","))
                        skipPrint = true
                    }
                    is Command.SetAi -> when(board.turn) {
                        Color.Light->
                            lightAI = ChesstasticAI(command.depth, command.breadth)
                        Color.Dark ->
                            darkAI = ChesstasticAI(command.depth, command.breadth)
                    }
                    is Command.Test -> {
                        ChessTests.run()
                        skipPrint = true
                    }
                    is Command.Load -> {
                        board = Board.parseHistory(command.history)
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
