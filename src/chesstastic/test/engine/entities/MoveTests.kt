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

            it("should read and write basic moves") {
                Move.Basic(Coordinate(E, _2), Coordinate(E, _4))
                    .shouldSerializeTo("E2E4")
            }

            it("should read and write kingside castle") {
                val lightMove = Move.KingsideCastle(Color.Light)
                lightMove.rook.shouldBe(Move.Basic(Coordinate(H, _1), Coordinate(F, _1)))
                lightMove.shouldSerializeTo("kcl")

                val darkMove = Move.KingsideCastle(Color.Light)
                darkMove.rook.shouldBe(Move.Basic(Coordinate(H, _8), Coordinate(F, _8)))
                darkMove.shouldSerializeTo("kcd")
            }

            it("should read and write queenside castle") {
                val lightMove = Move.QueensideCastle(Color.Light)
                lightMove.rook.shouldBe(Move.Basic(Coordinate(A, _1), Coordinate(D, _1)))
                lightMove.shouldSerializeTo("qcl")

                val darkMove = Move.QueensideCastle(Color.Light)
                darkMove.rook.shouldBe(Move.Basic(Coordinate(A, _8), Coordinate(D, _8)))
                darkMove.shouldSerializeTo("qcd")
            }

            it("should read and write en passant") {
                val move = Move.EnPassant(Coordinate(D, _5), Coordinate(C,_6))
                move.captured.shouldBe(Coordinate(C, _5))
                move.shouldSerializeTo("epD5C6")
            }

            it("should read and write pawn promotion") {
                Move.Promotion(Coordinate(D, _2), Coordinate(D, _1), Knight(Color.Dark))
                    .shouldSerializeTo("pdkD2D1")
                Move.Promotion(Coordinate(E, _7), Coordinate(F, _8), Knight(Color.Light))
                    .shouldSerializeTo("plkE7F8")
                Move.Promotion(Coordinate(D, _2), Coordinate(D, _1), Queen(Color.Dark))
                    .shouldSerializeTo("pdqD2D1")
                Move.Promotion(Coordinate(E, _7), Coordinate(F, _8), Queen(Color.Light))
                    .shouldSerializeTo("plqE7F8")
            }
        }
    }
}
