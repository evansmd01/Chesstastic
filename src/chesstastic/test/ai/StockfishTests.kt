package chesstastic.test.ai

import chesstastic.engine.entities.Board
import chesstastic.test.framework.ChessTestSuite
import chesstastic.ai.stockfish.Stockfish
import java.time.Duration

class StockfishTests: ChessTestSuite(){
    init {
        describe("selectMove") {
            it("should select a legal move") {
                val board = Board.parseHistory("e2e4 e7e5")

                val move = Stockfish(Duration.ofMillis(250)).selectMove(board)

                board.legalMoves.shouldContain(move)
            }
        }
    }
}
