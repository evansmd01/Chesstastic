package chesstastic.test.engine.rules.pieces

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.File.*
import chesstastic.engine.rules.pieces.PawnMoveCalculator
import chesstastic.test.framework.ChessTestSuite

class PawnMoveCalculatorTests: ChessTestSuite() {
    init {
        describe("attackingSquares") {
            it("should attack diagonals") {
                val board = Board.parseHistory("E2E4,D7D5,E4D5,C7C5,D5D6,C5C4,D6D7,B7B6")
                val calc = PawnMoveCalculator(Pawn(Color.Light), Square(D, _7), board)

                val diagonalOne =Square(C, _8)
                val diagonalTwo =Square(E, _8)

                val attackedSquares = calc.attackingSquares

                (diagonalOne in attackedSquares).shouldBe(true)
                (diagonalTwo in attackedSquares).shouldBe(true)
            }
        }

        describe("legalMoves") {
            it("should allow diagonal attacks") {
                val board = Board.parseHistory("E2E3,D7D5,E3E4,F7F5")
                val calc = PawnMoveCalculator(Pawn(Color.Light), Square(E, _4), board)

                val diagonalOne = Move.Basic(Square(E, _4), Square(D, _5))
                val diagonalTwo = Move.Basic(Square(E, _4), Square(F, _5))

                val legalMoves = calc.legalMoves

                (diagonalOne in legalMoves).shouldBe(true)
                (diagonalTwo in legalMoves).shouldBe(true)
            }

            it("should allow en passant") {
                val board = Board.parseHistory("E2E4,D7D5,E4D5,C7C5")
                val calc = PawnMoveCalculator(Pawn(Color.Light), Square(D, _5), board)

                val enPassant = Move.EnPassant(Square(D, _5), Square(C, _6))

                (enPassant in calc.legalMoves).shouldBe(true)
            }

            it("should allow pawn promotions") {
                val board = Board.parseHistory("E2E4,D7D5,E4D5,C7C5,D5D6,C5C4,D6D7,C4C3")

                val toQueen = Move.Promotion(Square(D, _7), Square(C, _8), Queen(Color.Light))
                val toKnight = Move.Promotion(Square(D, _7), Square(C, _8), Knight(Color.Light))

                val calc = PawnMoveCalculator(Pawn(Color.Light), Square(D, _7), board)

                (toQueen in calc.legalMoves).shouldBe(true)
                (toKnight in calc.legalMoves).shouldBe(true)
            }
        }
    }
}
