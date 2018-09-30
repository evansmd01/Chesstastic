package chesstastic.cli

import chesstastic.ai.AIPlayer
import chesstastic.ai.Chesstastic
import chesstastic.ai.stockfish.Stockfish
import chesstastic.cli.commands.Command
import chesstastic.cli.view.BoardView
import chesstastic.cli.view.ColumnsView
import chesstastic.cli.view.EvaluationView
import chesstastic.cli.view.ScoreView
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
        var promptAiMoves = true
        gameLoop@ while (true) {
            println()
            println()
            if (skipPrint) skipPrint = false
            else {
                val evaluationView = EvaluationView.render(Chesstastic().evaluate(board))
                val boardView = BoardView.render(board)
                val columnsView = ColumnsView.render(boardView, evaluationView)
                println(columnsView)
            }
            if (board.metadata.isCheckmate) {
                println(board.historyMetadata.history)
                break@gameLoop
            }
            if (board.metadata.isStalemate) {
                println(board.historyMetadata.history)
                break@gameLoop
            }
            val lastMove = board.historyMetadata.history.mostRecent
            print("${if(lastMove != null) "${board.historyMetadata.currentTurn.opposite} played $lastMove. " else ""}${board.historyMetadata.currentTurn} player's turn. Enter a move: ")
            val ai = if (board.historyMetadata.currentTurn == Color.Light) lightAI else darkAI
            when  {
                ai != null -> {
                    board = board.updatedWithoutValidation(ai.selectMove(board))
                    if(promptAiMoves) {
                        println()
                        printlnYellow("Press enter for next move, or type 'auto' to stop being prompted.")
                        val response = readLine()
                        if(response?.toLowerCase() == "auto") {
                            promptAiMoves = false
                        }
                    }
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
