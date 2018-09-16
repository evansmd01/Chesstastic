package chesstastic.test.engine.entities

import chesstastic.engine.entities.*
import chesstastic.test.framework.ChessTestSuite

class BoardTests: ChessTestSuite() {
    init {
        describe("parseHistory") {
            it("parses history correctly") {
                val testHistory = "E2E4,D7D5"
                val board = Board.parseHistory(testHistory)

                board.turn.shouldBe(Color.Light)
                board[Square(File.E, Rank._2)].shouldBe(null)
                board[Square(File.E, Rank._4)].shouldBe(Pawn(Color.Light))
                board[Square(File.D, Rank._7)].shouldBe(null)
                board[Square(File.D, Rank._5)].shouldBe(Pawn(Color.Dark))
            }
        }

        describe("isCheck") {
            it("should detect pawn check") {
                val board = Board.parseHistory("E2E4,D7D5,E4D5,C7C5,D5D6,C5C4,D6D7")
                board.isCheck.shouldBe(true)
            }
        }

        describe("isCheckmate") {
            it("should detect checkmate") {
                // TODO: updated scenario when more than pawn moves are possible
                val board = Board.parseHistory("E2E4,D7D5,E4D5,C7C5,D5D6,C5C4,D6D7")
                board.isCheckmate.shouldBe(true)
            }
        }

        describe("isStalemate") {
            it("should detect stalemate due to no legal moves") {
                // TODO
            }

            it("should detect stalemate due to inactivity") {
                // TODO
            }
        }
    }
}
