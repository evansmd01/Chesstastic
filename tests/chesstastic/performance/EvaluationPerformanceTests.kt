package chesstastic.performance

import chesstastic.ai.heuristics.Score
import chesstastic.ai.training.TrainingDataFile
import chesstastic.engine.entities.Board
import chesstastic.testing.framework.ChessTestSuite
import chesstastic.util.Stopwatch

class EvaluationPerformanceTests: ChessTestSuite() {
    init {
        describe("evaluating the scores of ever position in 20 games") {
            it("records performance") {
                val filename = "100-games.txt"
                val file = TrainingDataFile(filename)
                var positionCount = 0
                val duration = Stopwatch.timeAction {
                    file.readAllHistory().flatMap { moves ->
                        var board = Board()
                        moves.asSequence().map { move ->
                            // just updating the board should kick off the evaluation algorithm
                            // since it's needed to grab legal moves
                            board = board.updated(move)
                            positionCount++
                            // todo: grab board score to really be sure evaluation got kicked off.
                            val metadata = board.metadata
                        }
                    }.toList() // synchronously waits for the sequence to fully materialize
                }
                PerformanceLog.log(
                    description = "Evaluate all positions in $filename",
                    duration = duration,
                    positionsEvaluated = positionCount)
            }
        }
    }
}

