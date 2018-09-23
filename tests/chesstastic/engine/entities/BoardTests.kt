package chesstastic.engine.entities

import chesstastic.testing.framework.ChessTestSuite

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
                val board = Snapshot.parse("""
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p|p|Q|p|p|
                    | | | | | | | | |
                    | | | | | | |N| |
                    | | | | | | | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R|N|B| |K|B| |R|
                """.trimIndent(), turn = Color.Dark)

                board.isCheckmate.shouldBe(true)
            }
        }

        describe("isStalemate") {
            it("should detect stalemate due to no legal moves") {
                val board = Snapshot.parse("""
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | |k| | | | |
                    | | | | | | | | |
                    | | | |K| | | | |
                    | | | | | |q| | |
                    | | |r| | | | | |
                    | | | | | | | | |
                """.trimIndent(), turn = Color.Light)

                board.isStalemate.shouldBe(true)
            }

            it("should detect stalemate due to only kings") {
                val board = Snapshot.parse("""
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | |k| | | | |
                    | | | | | | | | |
                    | | | | |K| | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                """.trimIndent(), turn = Color.Light)

                board.isStalemate.shouldBe(true)
            }

            it("should detect stalemate due to inactivity") {
                val repeatMoves = (0..25).joinToString(separator = "") { "D1E2,D8D7,E2D1,D7D8," }
                val board = Board.parseHistory("E2E4,D7D5,$repeatMoves")
                board.isStalemate.shouldBe(true)
            }
        }
    }
}
