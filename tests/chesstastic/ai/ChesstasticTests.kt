package chesstastic.ai

import chesstastic.ai.heuristics.Heuristic
import chesstastic.ai.models.Imbalance
import chesstastic.ai.models.Score
import chesstastic.engine.entities.*
import chesstastic.tests.framework.ChessTestSuite
import java.util.concurrent.ThreadLocalRandom

@Suppress("unused")
class ChesstasticTests: ChessTestSuite() {
    init {
        describe("select move") {
            it("should select the move that results in the best possible score") {
                val board = Board()
                val mock = MockHeuristic()
                val subject = Chesstastic(ChesstasticConfig(3, 3, setOf(mock)))

                val selectedMove = subject.selectMove(board)

                val bestEvaluation = mock.bestEvaluationFor(board.historyMetadata.currentTurn)
                val bestFirstMove = bestEvaluation.board.historyMetadata.history.first()!!

                selectedMove.shouldBe(bestFirstMove)
            }
        }

        describe("full game") {
            it("should be able to play a full game without encountering errors") {
                var board = Board()

                val player1 = Chesstastic()
                val player2 = Chesstastic()

                while(!board.metadata.isCheckmate && !board.metadata.isStalemate) {
                    val player = if (board.historyMetadata.currentTurn == Color.Light) player1 else player2
                    board = board.updatedWithoutValidation(player.selectMove(board))
                }
            }
        }
    }
}

private class MockHeuristic: Heuristic {
    override val weights: Weights = Weights(emptyMap())
    override val key = Weights.Key.MATERIAL

    private val records = mutableListOf<EvaluationRecord>()

    fun bestEvaluationFor(color: Color): EvaluationRecord {
        // the best move is the move that results in the best position
        // the last move to be evaluated will be the opponents response.
        // so the best branch should be chosen from the branch that results
        // in the opponents last move leaving the player in the best possible position
        val maxMoveNumber = records.asSequence().map { it.board.historyMetadata.moveCount }.max() ?: 0
        val lastMoves = records.filter { it.board.historyMetadata.moveCount == maxMoveNumber }

        // every potential last move for the opponent should have been evaluated,
        // for each board that resulted from the players move
        // So for each board, the opponent will have chosen the move that puts
        // the player in the worst position.
        val lastMovesByBoard = lastMoves.groupBy {
            // drop last because we want to group by the board this move was in response to
            it.board.historyMetadata.history.previous
        }
        val bestOpponentMovePerBoard = lastMovesByBoard.mapNotNull { move -> move.value.maxBy { it.score.ratioInFavorOf(color.opposite) } }

        // Given the opponent's best responses to each potential board
        // The player should choose to go down the path that leads to
        // the best position after opponent's response
        val bestOptionForPlayer = bestOpponentMovePerBoard.maxBy { it.score.ratioInFavorOf(color) }

        return bestOptionForPlayer!!
    }

    override fun calculateImbalance(board: Board): Imbalance {
        val light = ThreadLocalRandom.current().nextDouble()
        val dark = ThreadLocalRandom.current().nextDouble()
        val imbalance = Imbalance(light, dark)
        records.add(EvaluationRecord(imbalance.score, board))
        return imbalance
    }

    data class EvaluationRecord(val score: Score, val board: Board)
}


