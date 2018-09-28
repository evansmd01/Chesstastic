package chesstastic.engine

import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color
import chesstastic.engine.entities.PieceKind
import chesstastic.engine.entities.Rank
import chesstastic.testing.framework.ChessTestSuite
import chesstastic.util.Snapshot

@Suppress("unused")
class BoardTests: ChessTestSuite() {
    init {
        describe("parseHistory") {
            it("parses historyMetadata correctly") {
                val testHistory = "E2E4,D7D5"
                val board = Board.parseHistory(testHistory)

                board.historyMetadata.currentTurn.shouldBe(Color.Light)

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

                board.metadata.isCheck.shouldBe(true)
            }

            it("should detect knight check") {
                val board = Snapshot.parse("""
                    |r|n|b|q| |b|n| |
                    | |p|p|k| | | | |
                    |p| | |p| |p| |r|
                    | | | | |N| |p| |
                    | | | |P| | | |N|
                    | | | |K| | | | |
                    |P|P|P| |P|P|P|P|
                    |R| | |Q| |B| |R|
                """.trimIndent(), turn = Color.Dark)

                val result = board.metadata.isCheck
                result.shouldBe(true)
            }

            it("should detect queen check") {
                val board = Snapshot.parse("""
                    |r|n|b| |k| | |r|
                    |p| | |p| |p|p|p|
                    | | |p| |p| | | |
                    | | | | | | |q| |
                    | |P| | | | | | |
                    |P| | | | | |K|P|
                    |R| |P|P|B| |P| |
                    | |N|B|Q| | |N|R|
                """.trimIndent(), turn = Color.Light)

                val result = board.metadata.isCheck
                result.shouldBe(true)
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

                val result = board.metadata.isCheckmate
                result.shouldBe(true)
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

                board.metadata.isStalemate.shouldBe(true)
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

                board.metadata.isStalemate.shouldBe(true)
            }

            it("should detect stalemate due to inactivity") {
                val repeatMoves = (0..25).joinToString(separator = "") { "D1E2,D8D7,E2D1,D7D8," }
                val board = Board.parseHistory("E2E4,D7D5,$repeatMoves")
                board.metadata.isStalemate.shouldBe(true)
            }
        }

        describe("metadata") {
            describe("player moves") {
                it("Can't make a king move that stays in check, when in check") {
                    val board = Snapshot.parse("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | |k| |R|p| | | |
                        | | | | | | | |P|
                        | | | | | | | | |
                        | | |P| |K| | | |
                        | | | | | | | | |
                     """.trimIndent(), turn = Color.Dark)

                    val hasIllegalMove = board.metadata.darkPlayer.moves.kingMoves.any { it.move.to.rank == Rank._5 }
                    hasIllegalMove.shouldBe(false)
                }

                it("Can't move the queen through pieces") {
                    val board = Snapshot.parse("""
                        |r|n|b|q|k|b|n|r|
                        |p| | |p|p|p| |p|
                        | | |p| | | | | |
                        | |p| | | | |p| |
                        |Q| |P| | | |P| |
                        | | | | | | | | |
                        |P|P| |P|P|P| |P|
                        |R|N|B| |K|B|N|R|
                     """.trimIndent(), turn = Color.Light)

                    val canCaptureKing = board.metadata.lightPlayer.moves.queenMoves.any { it.capturing?.piece?.kind == PieceKind.King }

                    canCaptureKing.shouldBe(false)
                }
            }
        }
    }
}
