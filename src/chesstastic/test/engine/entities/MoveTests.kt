package chesstastic.test.engine.entities

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.File.*
import chesstastic.test.framework.ChessTestSuite



class MoveTests: ChessTestSuite() {
    init {
        describe("string serialization") {
            fun Move.shouldSerializeTo(expectation: String) {
                val serialized = this.toString()
                serialized.shouldBe(expectation)
                val deserialized = Move.parse(serialized)
                deserialized.shouldBe(this)
            }

            it("should read and write basic legalMoves") {
                Move.Basic(Square(E, _2), Square(E, _4))
                    .shouldSerializeTo("E2E4")
            }

            it("should read and write kingside castle") {
                val lightMove = Move.KingsideCastle(Color.Light)
                lightMove.rook.shouldBe(Move.Basic(Square(H, _1), Square(F, _1)))
                lightMove.shouldSerializeTo("kcl")

                val darkMove = Move.KingsideCastle(Color.Dark)
                darkMove.rook.shouldBe(Move.Basic(Square(H, _8), Square(F, _8)))
                darkMove.shouldSerializeTo("kcd")
            }

            it("should read and write queenside castle") {
                val lightMove = Move.QueensideCastle(Color.Light)
                lightMove.rook.shouldBe(Move.Basic(Square(A, _1), Square(D, _1)))
                lightMove.shouldSerializeTo("qcl")

                val darkMove = Move.QueensideCastle(Color.Dark)
                darkMove.rook.shouldBe(Move.Basic(Square(A, _8), Square(D, _8)))
                darkMove.shouldSerializeTo("qcd")
            }

            it("should read and write en passant") {
                val move = Move.EnPassant(Square(D, _5), Square(C,_6))
                move.captured.shouldBe(Square(C, _5))
                move.shouldSerializeTo("epD5C6")
            }

            it("should read and write pawn promotion") {
                Move.Promotion(Square(D, _2), Square(D, _1), Knight(Color.Dark))
                    .shouldSerializeTo("pdkD2D1")
                Move.Promotion(Square(E, _7), Square(F, _8), Knight(Color.Light))
                    .shouldSerializeTo("plkE7F8")
                Move.Promotion(Square(D, _2), Square(D, _1), Queen(Color.Dark))
                    .shouldSerializeTo("pdqD2D1")
                Move.Promotion(Square(E, _7), Square(F, _8), Queen(Color.Light))
                    .shouldSerializeTo("plqE7F8")
            }

            describe("parse many") {
                it("should parse two legalMoves") {
                    val testHistory = "E2E4,D7D5"
                    val moves = Move.parseMany(testHistory)
                    moves.shouldBe(listOf(
                        Move.Basic(Square(E, _2), Square(E, _4)),
                        Move.Basic(Square(D, _7), Square(D, _5))
                    ))
                }
            }
        }
    }
}


