package chesstastic.engine.calculators

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.File.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.entities.PieceKind.*
import chesstastic.testing.framework.ChessTestSuite

class PawnCalculatorTests: ChessTestSuite() {
    init {
        describe("timeSquareIsAttacked") {
            it("detects single attacks") {
                val board = Board.parseHistory("E2E4,D7D5")

                val result = PawnCalculator.attackers(Square(D, _5),
                    attacker = Light, board = board).size

                result.shouldBe(1)
            }

            it("detects multiple attacks") {
                val board = Board.parseHistory("E2E4,D7D5,H2H3,F7F5")

                val result = PawnCalculator.attackers(Square(E, _4),
                    attacker = Dark, board = board).size

                result.shouldBe(2)
            }
        }

        describe("potentialMoves") {
            it("should allow diagonal attacks") {
                val board = Board.parseHistory("E2E3,D7D5,E3E4,F7F5")

                val diagonalOne = Move.Basic(Square(E, _4), Square(D, _5))
                val diagonalTwo = Move.Basic(Square(E, _4), Square(F, _5))

                val legalMoves = PawnCalculator.potentialMoves(Light, Square(E, _4), board)

                (diagonalOne in legalMoves).shouldBe(true)
                (diagonalTwo in legalMoves).shouldBe(true)
            }

            it("should allow en passant") {
                val board = Board.parseHistory("E2E4,D7D5,E4D5,C7C5")

                val legalMoves = PawnCalculator.potentialMoves(Light, Square(D, _5), board)

                val enPassant = Move.EnPassant(Square(D, _5), Square(C, _6))

                (enPassant in legalMoves).shouldBe(true)
            }

            it("should allow pawn promotions") {
                val board = Board.parseHistory("C2C8,C8C7")
                val pawnSquare = Square(C, _7)

                val promotionSquares = listOf(Square(B, _8), Square(C, _8), Square(D, _8))
                val promotions = promotionSquares.flatMap { listOf(
                    Move.Promotion(pawnSquare, it, Queen),
                    Move.Promotion(pawnSquare, it, Knight)
                ) }

                val legalMoves = PawnCalculator.potentialMoves(Light, pawnSquare, board)

                legalMoves.shouldBeEquivalentTo(promotions)
            }
        }
    }
}
