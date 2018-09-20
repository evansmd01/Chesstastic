package chesstastic.test.ai

import chesstastic.ai.*
import chesstastic.ai.criteria.Criteria
import chesstastic.ai.values.Score
import chesstastic.engine.entities.*
import chesstastic.test.framework.ChessTestSuite
import java.util.concurrent.ThreadLocalRandom

class ChesstasticAITests: ChessTestSuite() {
    init {
        describe("select move") {
            it("should select the move that results in the best possible score") {
                val board = Board.createNew()
                val mock = MockCriteria()
                val subject = ChesstasticAI(10, 10, criteria = listOf(mock))

                val selectedMove: Move = subject.selectMove(board)

                val bestEvaluation: EvaluationRecord = mock.bestEvaluationFor(board.turn)
                val bestFirstMove: Move = bestEvaluation.board.history.first()

                selectedMove.shouldBe(bestFirstMove)
            }
        }

        describe("full game") {
            it("should be able to play a full game without encountering errors") {
                var board = Board.createNew()

                val player1 = ChesstasticAI(2, 2)
                val player2 = ChesstasticAI(2, 2)

                while(!board.isGameOver) {
                    val player = if (board.turn == Color.Light) player1 else player2
                    board = board.updated(player.selectMove(board))
                }
            }
        }
    }
}

class MockCriteria: Criteria {
    val records = mutableListOf<EvaluationRecord>()

    fun bestEvaluationFor(color: Color): EvaluationRecord {
        // the best move is the move that results in the best position
        // the last move to be evaluated will be the opponents response.
        // so the best branch should be chosen from the branch that results
        // in the opponents last move leaving the player in the best possible position
        val maxMoveNumber = records.map { it.board.history.size }.max() ?: 0
        val lastMoves = records.filter { it.board.history.size == maxMoveNumber }

        // every potential last move for the opponent should have been evaluated,
        // for each board that resulted from the players move
        // So for each board, the opponent will have chosen the move that puts
        // the player in the worst position.
        val lastMovesByBoard = lastMoves.groupBy {
            // drop last because we want to group by the board this move was in response to
            it.board.history.dropLast(1)
        }
        val bestOpponentMovePerBoard = lastMovesByBoard.mapNotNull { it.value.maxBy { it.score.ratioInFavorOf(color.opposite) } }

        // Given the opponent's best responses to each potential board
        // The player should choose to go down the path that leads to
        // the best position after opponent's response
        val bestOptionForPlayer = bestOpponentMovePerBoard.maxBy { it.score.ratioInFavorOf(color) }

        return bestOptionForPlayer!!
    }

    override fun evaluate(board: Board): Score {
        val light = ThreadLocalRandom.current().nextDouble()
        val dark = ThreadLocalRandom.current().nextDouble()
        val score = Score(light, dark)
        records.add(EvaluationRecord(score, board))
        return score
    }

}

data class EvaluationRecord(val score: Score, val board: Board)
