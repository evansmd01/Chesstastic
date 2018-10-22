package chesstastic.ai

import chesstastic.ai.heuristics.Heuristic
import chesstastic.ai.models.Score
import chesstastic.engine.entities.*
import chesstastic.tests.framework.ChessTestSuite
import java.util.concurrent.ThreadLocalRandom

@Suppress("unused")
class ChesstasticTests: ChessTestSuite() {
    init {
        describe("full game", focus = true) {
            it("should be able to play a full game without encountering errors") {
                var board = Board()

                val player1 = Chesstastic.DEFAULT
                val player2 = Chesstastic.DEFAULT

                while(!board.metadata.isCheckmate && !board.metadata.isStalemate) {
                    val player = if (board.historyMetadata.currentTurn == Color.Light) player1 else player2
                    board = board.updatedWithoutValidation(player.selectMove(board))
                }
            }
        }
    }
}


