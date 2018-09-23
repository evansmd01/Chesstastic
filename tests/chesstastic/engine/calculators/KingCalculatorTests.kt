package chesstastic.engine.calculators

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.File.*
import chesstastic.engine.entities.Color.*
import chesstastic.testing.framework.ChessTestSuite

class KingCalculatorTests: ChessTestSuite() {
    init {
        describe("potentialMoves") {
            it("should move any square in one direction") {
                val board = Board.parseHistory("E1E4")
                val kingSquare = board.kingSquare(Light)

                val expectedMoves = listOf(
                    Square(E, _5), Square(E, _3),
                    Square(F, _5), Square(F, _4), Square(F, _3),
                    Square(D, _5), Square(D, _4), Square(D, _3)
                ).map { Move.Basic(kingSquare, it)}

                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldBeEquivalentTo(expectedMoves)
            }

            it("should be blocked by it's own pieces") {
                val board = Board.parseHistory("E1E4,D2D5,E2E5,F2F5,F1F4,D1D4,G1E3,G2F3,C2D3")
                val kingSquare = board.kingSquare(Light)

                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.count().shouldBe(0)
            }

            it("should be able to capture") {
                val board = Board.parseHistory("E1B7,D7A6,E7B6,F7C6")
                val kingSquare = board.kingSquare(Light)

                val expectedMoves = listOf(
                    Square(B, _6), Square(B, _8),
                    Square(C, _6), Square(C, _7), Square(C, _8),
                    Square(A, _6), Square(A, _7), Square(A, _8)
                ).map { Move.Basic(kingSquare, it)}

                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldBeEquivalentTo(expectedMoves)
            }

            it("should be able to kingside castle") {
                val board = Board.parseHistory("A1A3,A3A1,F1F3,G1G3")
                val kingSquare = board.kingSquare(Light)

                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldContain(Move.KingsideCastle(Light))
            }

            it("should not kingside castle if the rook has moved before") {
                val board = Board.parseHistory("F1F3,G1G3,H1H3,H3H1")
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.KingsideCastle }
            }

            it("should not kingside castle if moving through check") {
                val board = Board.parseHistory("F1F3,G1G3,E7E2")
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.KingsideCastle }
            }

            it("should be able to queenside castle") {
                val board = Board.parseHistory("H1H3,H3H1,B1B3,C1C3,D1D3")
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldContain(Move.QueensideCastle(Light))
            }

            it("should not queenside castle if the rook has moved before") {
                val board = Board.parseHistory("A1A3,A3A1,B1B3,C1C3,D1D3")
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.QueensideCastle }
            }

            it("should not queenside castle if moving through check") {
                val board = Board.parseHistory("B1B3,C1C3,D1D3,C7C2")
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.QueensideCastle }
            }

            it("should not castle either way if the king has moved before") {
                val board = Board.parseHistory("B1B3,C1C3,D1D3,F1F3,G1G3,E1D1,D1E1")
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.Castle }
            }

            it("should not castle if the king is blocked by his own pieces") {
                val board = Board.parseHistory("E2E4")
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.Castle }
            }

            it("should not be allowed to castle to get out of check") {
                val board = Board.parseHistory("F1F3,G1G3,D7D2")
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.Castle }
            }
        }

        describe("timesSquareIsAttacked") {
            it("should attack any adjacent square") {
                val board = Board.parseHistory("E1B7,D7A6,E7B6,F7C6")

                val expectedAttacks = listOf(
                    Square(B, _6), Square(B, _8),
                    Square(C, _6), Square(C, _7), Square(C, _8),
                    Square(A, _6), Square(A, _7), Square(A, _8)
                )

                val attackedSquares = Board.SQUARES.filter {
                    KingCalculator.attackers(it, Light, board).size == 1
                }

                attackedSquares.shouldBeEquivalentTo(expectedAttacks)
            }
        }
    }
}
