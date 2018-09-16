package chesstastic.test.engine.rules

import chesstastic.cli.commands.Command
import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.File.*
import chesstastic.engine.rules.MoveCalculator
import chesstastic.engine.rules.pieces.PawnMoveCalculator
import chesstastic.test.framework.ChessTestSuite

class MoveCalculatorTests: ChessTestSuite() {
    init {
        describe("legalMoves") {
            it("should contain pawn promotion legalMoves") {
                val board = Board.parseHistory("E2E4,D7D5,E4D5,C7C5,D5D6,C5C4,D6D7,C4C3")
                val toQueen = Move.Promotion(Square(D, _7), Square(C, _8), Queen(Color.Light))
                val toKnight = Move.Promotion(Square(D, _7), Square(C, _8), Knight(Color.Light))

                val legalMoves = MoveCalculator.legalMoves(board)

                (toQueen in legalMoves).shouldBe(true)
                (toKnight in legalMoves).shouldBe(true)
            }

            it("should allow en passant") {
                val board = Board.parseHistory("E2E4,D7D5,E4D5,C7C5")
                val enPassant = Move.EnPassant(Square(D, _5), Square(C, _6))

                val legalMoves = MoveCalculator.legalMoves(board)

                (enPassant in legalMoves).shouldBe(true)
            }
        }

        describe("isKingInCheck") {
            it("should detect king in check by pawn") {
                val board = Board.parseHistory("E2E4,D7D5,E4D5,C7C5,D5D6,C5C4,D6D7")

                val isLightKingInCheck = MoveCalculator.isKingInCheck(Color.Light, board)
                isLightKingInCheck.shouldBe(false)

                val isDarkKingInCheck = MoveCalculator.isKingInCheck(Color.Dark, board)
                isDarkKingInCheck.shouldBe(true)
            }
        }

        describe("isSquareAttacked") {
            it("should detect pawn attacks") {
                val board = Board.parseHistory("E2E4,D7D5,E4D5,C7C5,D5D6,C5C4,D6D7")

                val square = Square(E, _8)

                val result = MoveCalculator.isSquareAttacked(square, Color.Light, board)
                result.shouldBe(true)
            }
        }
    }
}
