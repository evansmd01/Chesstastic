package chesstastic.tasks

import chesstastic.ai.AIPlayer
import chesstastic.ai.stockfish.Stockfish
import chesstastic.ai.training.TrainingDataFile
import chesstastic.engine.entities.Board
import java.io.File
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom

object GameGenerator: Task {
    override val name = "generate games"

    private fun outputFilePath() =
        "${System.getProperty("user.dir")}/data/generated-boards-${System.currentTimeMillis() / 1000L}.txt"

    override fun execute() {
        val file = File(outputFilePath())
        file.parentFile.mkdirs()
        val dataFile = TrainingDataFile(file)
        val ai = Stockfish(Duration.ofMillis(50))
        dataFile.writeAll(ai.generateGames(20).map {
            println()
            println("Generated: " + it.historyMetadata.history.toString())
            it
        })
    }

    private fun AIPlayer.generateGames(amount: Int): Sequence<Board> {
        var count = 0
        return generateSequence {
            when {
                count >= amount -> null
                else -> {
                    count++
                    playGame(randomSeedBoard())
                }
            }
        }
    }

    private fun randomSeedBoard(): Board {
        var board = Board()
        for(i in 0..5) {
            val moves = board.legalMoves.toList()
            val index = ThreadLocalRandom.current().nextInt(0, moves.size - 1)
            board = board.updatedWithoutValidation(moves[index])
        }
        return board
    }

    private fun AIPlayer.playGame(board: Board): Board {
        return when {
            board.isGameOver -> board
            else -> {
                val nextMove = selectMove(board)
                print("$nextMove ")
                playGame(board.updated(selectMove(board)))
            }
        }
    }
}

