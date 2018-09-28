package chesstastic.cli

import chesstastic.ai.AIPlayer
import chesstastic.ai.Chesstastic
import chesstastic.ai.stockfish.Stockfish
import chesstastic.cli.commands.Command
import chesstastic.cli.view.BoardView
import chesstastic.engine.entities.*
import chesstastic.tasks.Task
import chesstastic.util.*
import java.time.Duration

object CliGameLoop {
    fun start() {
        var board = Board()
        var skipPrint = false
        var lightAI: AIPlayer? = null
        var darkAI: AIPlayer? = null
        gameLoop@ while (true) {

            println()
            println()
            if (skipPrint) skipPrint = false
            else println(BoardView.render(board))
            if (board.metadata.isCheckmate) {
                println(board.historyMetadata.history)
                break@gameLoop
            }
            if (board.metadata.isStalemate) {
                println(board.historyMetadata.history)
                break@gameLoop
            }
            print("${board.historyMetadata.currentTurn} player's turn: ")
            val ai = if (board.historyMetadata.currentTurn == Color.Light) lightAI else darkAI
            when  {
                ai != null -> {
                    board = board.updatedWithoutValidation(ai.selectMove(board))
                }
                else -> {
                    val input = readLine()?.toLowerCase()?.trim()
                    val command = input?.let { Command.parse(it) }
                    when (command) {
                        is Command.Import -> {
                            board = Board.parseHistory(command.history)
                        }
                        is Command.Export -> {
                            println()
                            println("State:\n" + Snapshot.from(board))
                            println()
                            println("History:\n" + board.historyMetadata.history)
                            skipPrint = true
                        }
                        is Command.RunTask -> {
                            val task = Task.get(command.taskName)
                            if (task != null) task.execute()
                            else printlnRed("Invalid task name: ${command.taskName}")
                            skipPrint = true
                        }
                        is Command.SetAi -> when(board.historyMetadata.currentTurn) {
                            Color.Light->
                                lightAI = Chesstastic(command.depth, command.breadth)
                            Color.Dark ->
                                darkAI = Chesstastic(command.depth, command.breadth)
                        }
                        is Command.SetStockfish -> when(board.historyMetadata.currentTurn) {
                            Color.Light ->
                                lightAI = Stockfish(Duration.ofMillis(command.moveTimeMillis))
                            Color.Dark ->
                                darkAI = Stockfish(Duration.ofMillis(command.moveTimeMillis))
                        }
                        is Command.MoveCommand -> {
                            val move = board.metadata.legalMoves.firstOrNull {
                                it == command.move
                            }
                            if (move != null)
                                board = board.updatedWithoutValidation(move)
                            else
                                printlnRed("Invalid move: $input")
                        }
                        is Command -> throw NotImplementedError("Command $input has not been handled in CliGameLoop")
                        else -> printlnRed("Invalid command: $input")
                    }
                }
            }
        }
    }
}
