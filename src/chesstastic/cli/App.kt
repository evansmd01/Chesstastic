package chesstastic.cli

import chesstastic.ai.AIPlayer
import chesstastic.ai.Chesstastic
import chesstastic.ai.stockfish.Stockfish
import chesstastic.cli.commands.Command
import chesstastic.cli.view.BoardView
import chesstastic.engine.entities.*
import chesstastic.util.*
import java.time.Duration

fun main(args: Array<String>) {
    var board = Board.createNew()
    var validateMoves = true
    var skipPrint = false
    var lightAI: AIPlayer? = null
    var darkAI: AIPlayer? = null
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
                    is Command.Export -> {
                        println()
                        println("State:\n" + Snapshot.from(board))
                        println()
                        println("History:\n" + board.history.joinToString(separator = " "))
                        skipPrint = true
                    }
                    is Command.SetAi -> when(board.turn) {
                        Color.Light->
                            lightAI = Chesstastic(command.depth, command.breadth)
                        Color.Dark ->
                            darkAI = Chesstastic(command.depth, command.breadth)
                    }
                    is Command.SetStockfish -> when(board.turn) {
                        Color.Light ->
                            lightAI = Stockfish(Duration.ofMillis(command.moveTimeMillis))
                        Color.Dark ->
                            darkAI = Stockfish(Duration.ofMillis(command.moveTimeMillis))
                    }
                    is Command.Import -> {
                        board = Board.parseHistory(command.history)
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
