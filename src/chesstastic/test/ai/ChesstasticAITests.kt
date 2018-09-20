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
                val subject = ChesstasticAI(2, 2, listOf(mock))

                val selectedMove: Move = subject.selectMove(board)

                val bestEvaluation: EvaluationRecord = mock.bestEvaluationFor(board.turn)
                val bestFirstMove: Move = bestEvaluation.board.history.first()

                if(selectedMove != bestFirstMove) {
                    println("Selected Move: $selectedMove")
                    println("Best Evaluation: $bestEvaluation")
                    println("Evaluations starting with selected move: " + mock.evaluationsStartingWith(selectedMove).joinToString(separator = "\n"))
                }

                selectedMove.shouldBe(bestFirstMove)
            }
        }
    }
}

class MockCriteria: Criteria {
    val records = mutableListOf<EvaluationRecord>()

    fun bestEvaluationFor(color: Color): EvaluationRecord {
        return records.filter { it.whoseMove == color }.maxBy { it.score.ratioInFavorOf(color) }!!
    }

    fun evaluationsStartingWith(move: Move) = records.filter { it.board.history.first() == move }.sortedBy { it.board.history.count() }

    override fun evaluate(board: Board): Score {
        val light = ThreadLocalRandom.current().nextDouble()
        val dark = ThreadLocalRandom.current().nextDouble()
        val score = Score(light, dark)
        val whoseMove = board.turn.opposite // because if we're evaluating a move white made, it's currently blacks turn
        records.add(EvaluationRecord(whoseMove, score, board))
        return score
    }

}

data class EvaluationRecord(val whoseMove: Color, val score: Score, val board: Board) {
    override fun toString(): String {
        return "(whoseMove: $whoseMove, score: ${score.ratioInFavorOf(whoseMove)}, history: ${board.history.joinToString()})"
    }
}
