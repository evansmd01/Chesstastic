package chesstastic.engine

import chesstastic.engine.entities.*
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

                it("should be allowed to move king in front of pawn") {
                    val board = Snapshot.parse("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | |n| |k| |n|N| |
                        | | | |p| | | |p|
                        | | | |N|p| |p|P|
                        | | |P| | | |P| |
                        | | | |K| |P| | |
                        | | | | | | | | |
                     """.trimIndent(), turn = Color.Light)

                    board.metadata.lightPlayer.moves
                        .kingMoves.map { it.move }
                        .shouldContain(
                            Move.parse("d2e3")
                        )
                }

                it("should not be able to block check from a knight") {
                    val board = Snapshot.parse("""
                        | | | |k| | | |r|
                        |Q| | |n| |N| |p|
                        | |P| | | | | |n|
                        | | | |p| | | |P|
                        | | | | |P| | | |
                        |b| | |B| | |P| |
                        |P| |P| | |P| | |
                        | | | | |K| | |R|
                     """.trimIndent(), turn = Color.Dark)

                    board.metadata.legalMoves.shouldBeEquivalentTo(setOf(
                        Move.parse("h6f7"),
                        Move.parse("d8e7"),
                        Move.parse("d8e8"),
                        Move.parse("d8c8")
                    ))
                }

                it("should not be able to castle if the rook has been captured") {
                    val board = Snapshot.parse("""
                        |r| | | |k|b|n|r|
                        |p|p|q| | |p|p|p|
                        | | | | | | | | |
                        | | |n| |p| | | |
                        | | | |p|P| | | |
                        |P| | |P| |b| |B|
                        | |P|P| |N|P| |P|
                        |R| |B|Q|K| | |R|
                     """.trimIndent(), turn = Color.Dark, allowCastle = true)
                        .updated(Move.parse("f3h1")!!) // capture the rook

                    board.metadata.legalMoves.shouldNotContain(Move.parse("e1g1"))
                }

                it("should not allow castling while in check") {
                    val board = Snapshot.parse("""
                        |r| | | |k|b| |r|
                        |p|p|N| |p|p|p|p|
                        | | | | | | | |n|
                        | | | | | | | | |
                        | | | |P| | | | |
                        | | | | | | | | |
                        |P|P|P| | |P|P|P|
                        |R| |B|b|K|B| |R|
                     """.trimIndent(), turn = Color.Dark, allowCastle = true)

                    board.metadata.legalMoves.shouldNotContain(Move.parse("e8c8"))
                }

                it("should not allow castling through pieces") {
                    val board = Snapshot.parse("""
                        |r|n| | |k|b| |r|
                        |p|p| | |p|p|p|p|
                        | | | | | | | |n|
                        | | | | | | | | |
                        | | | |P| | | | |
                        | | | | | | | | |
                        |P|P|P| | |P|P|P|
                        |R| |B|b|K|B| |R|
                     """.trimIndent(), turn = Color.Dark, allowCastle = true)

                    board.metadata.legalMoves.shouldNotContain(Move.parse("e8c8"))
                }

                it("can't move a piece that doesn't escape check") {
                    val board = Snapshot.parse("""
                        |r| |b|q|k| |n|r|
                        |p|p|p|p|p| | |p|
                        | | | | | | | | |
                        | | | | |b|p| | |
                        | | | | | | | | |
                        | | | |P|P| |K| |
                        | | |P|N| | | |P|
                        | | |Q| | |B|N|R|
                     """.trimIndent(), turn = Color.Light)

                    board.metadata.legalMoves.shouldNotContain(Move.parse("c1d1"))
                }
            }

            describe("pins") {
                it("should not allow pieces to move if they are pinned to the king") {
                    val board = Snapshot.parse("""
                        |r|n| | | | | | |
                        |p|p| |k| |p|p| |
                        | | | | |p| | | |
                        | | |p| | | | | |
                        | | | | |p| | |N|
                        | | | | | | | |B|
                        |P| |Q|P| |P| | |
                        | |R| | |K| | |R|
                     """.trimIndent(), turn = Color.Dark)

                    board.metadata.squares[Square(File.E, Rank._6)]?.pins?.isNotEmpty().shouldBe(true)
                    board.metadata.legalMoves.shouldNotContain {
                        it == Move.Basic(Square(File.E, Rank._6), Square(File.E, Rank._5))
                    }
                }
            }
        }
    }
}
