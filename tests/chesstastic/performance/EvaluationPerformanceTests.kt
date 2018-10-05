package chesstastic.performance

import chesstastic.ai.Chesstastic
import chesstastic.ai.training.TrainingDataFile
import chesstastic.engine.entities.Board
import chesstastic.tests.framework.ChessTestSuite
import chesstastic.util.Stopwatch

@Suppress("unused")
class EvaluationPerformanceTests: ChessTestSuite() {
    init {
        val chesstastic = Chesstastic.DEFAULT
        describe("evaluating the scores of every position from 100 games") {
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
                            chesstastic.evaluate(board)
                        }
                    }.toList() //materializes/evaluates the sequence
                }
                PerformanceLog.log(
                    description = "Evaluate all positions in $filename",
                    duration = duration,
                    positionsEvaluated = positionCount)
            }
        }
    }
}

