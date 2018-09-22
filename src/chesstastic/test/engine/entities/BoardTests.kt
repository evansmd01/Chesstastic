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

                board.shouldMatch("""
                    |r|n|b|q|k|b|n|r|
                    |p|p|p| |p|p|p|p|
                    | | | | | | | | |
                    | | | |p| | | | |
                    | | | | |P| | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R|N|B|Q|K|B|N|R|
                """.trimIndent())
            }
        }

        describe("isCheck") {
            it("should detect pawn check") {
                val board = Snapshot.parse("""
                    |r|n|b|q|k|b|n|r|
                    |p|p| |P|p|p|p|p|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | |p| | | | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R|N|B|Q|K|B|N|R|
                """.trimIndent(), turn = Color.Dark)

                board.isCheck.shouldBe(true)
            }
        }

        describe("isCheckmate") {
            it("should detect checkmate") {
                // TODO
            }
        }

        describe("isStalemate") {
            it("should detect stalemate due to no legal potentialMoves") {
                // TODO
            }

            it("should detect stalemate due to inactivity") {
                // TODO
            }
        }
    }
}
