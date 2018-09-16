package chesstastic.test.engine.rules

import chesstastic.engine.entities.*
import chesstastic.engine.entities.File.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.rules.MoveCalculator
import chesstastic.test.framework.ChessTestSuite

class MoveCalculatorTests: ChessTestSuite() {
    init {
        describe("legalMoves") {
            it("should contain pawn promotion potentialMoves") {
                val board = Board.parse("E2E4,D7D5,E4D5,C7C5,D5D6,C5C4,D6D7,C4C3")
                val toQueen = Move.Promotion(Square(D, _7), Square(C, _8), Queen(Light))
                val toKnight = Move.Promotion(Square(D, _7), Square(C, _8), Knight(Light))

                val legalMoves = MoveCalculator.legalMoves(board)

                (toQueen in legalMoves).shouldBe(true)
                (toKnight in legalMoves).shouldBe(true)
            }

            it("should allow en passant") {
                val board = Board.parse("E2E4,D7D5,E4D5,C7C5")
                val enPassant = Move.EnPassant(Square(D, _5), Square(C, _6))

                val legalMoves = MoveCalculator.legalMoves(board)

                (enPassant in legalMoves).shouldBe(true)
            }
        }

        describe("isKingInCheck") {
            it("should detect king in check by pawn") {
                val board = Board.parse("E2E4,D7D5,E4D5,C7C5,D5D6,C5C4,D6D7")

                val isLightKingInCheck = MoveCalculator.isKingInCheck(Light, board)
                isLightKingInCheck.shouldBe(false)

                val isDarkKingInCheck = MoveCalculator.isKingInCheck(Dark, board)
                isDarkKingInCheck.shouldBe(true)
            }
        }

        describe("isSquareAttacked") {
            it("should detect pawn attacks") {
                val board = Board.parse("E2E4,D7D5,E4D5,C7C5,D5D6,C5C4,D6D7")

                val square = Square(E, _8)

                val result = MoveCalculator.isSquareAttacked(square, Light, board)
                result.shouldBe(true)
            }
        }

        describe("timesSquareIsAttacked") {
            it("should detect multiple attacks from pieces of the same type") {
                val board = Board.parse("E2E4,D7D5,H2H3,F7F5")

                val result = MoveCalculator.timesSquareIsAttacked(Square(E, _4),
                    attacker = Dark, board = board)

                result.shouldBe(2)
            }
        }
    }
}
