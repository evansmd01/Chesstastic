package chesstastic.engine.calculators

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.File.*
import chesstastic.engine.entities.Color.*
import chesstastic.testing.framework.ChessTestSuite

@Suppress("unused")
class KingCalculatorTests: ChessTestSuite() {
    init {
        describe("potentialMoves") {
            it("should move any square in one direction") {
                val board = Snapshot.parse("""
                    | | | | |k| | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | |K| | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                """.trimIndent(), turn = Color.Light)

                val kingSquare = board.kingSquare(Light)

                val expectedMoves = listOf(
                    Square(E, _5), Square(E, _3),
                    Square(F, _5), Square(F, _4), Square(F, _3),
                    Square(D, _5), Square(D, _4), Square(D, _3)
                ).map { Move.Basic(kingSquare, it)}

                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldBeEquivalentTo(expectedMoves)
            }

            it("should be blocked by it's own allPieces") {
                val board = Snapshot.parse("""
                    | | | | |k| | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | |P|P|P| | |
                    | | | |P|K|P| | |
                    | | | |P|P|P| | |
                    | | | | | | | | |
                    | | | | | | | | |
                """.trimIndent(), turn = Color.Light)
                val kingSquare = board.kingSquare(Light)

                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.count().shouldBe(0)
            }

            it("should be able to capture") {
                val board = Snapshot.parse("""
                    | | | | |k| | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | |p|p|p| | |
                    | | | |p|K|p| | |
                    | | | |p|p|p| | |
                    | | | | | | | | |
                    | | | | | | | | |
                """.trimIndent(), turn = Color.Light)

                val kingSquare = board.kingSquare(Light)

                val expectedMoves = listOf(
                    Square(E, _5), Square(E, _3),
                    Square(F, _5), Square(F, _4), Square(F, _3),
                    Square(D, _5), Square(D, _4), Square(D, _3)
                ).map { Move.Basic(kingSquare, it)}

                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldBeEquivalentTo(expectedMoves)
            }

            it("should be able to kingside castle") {
                val board = Snapshot.parse("""
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p|p|p|p|p|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |P|P|P|P|P|P|P|P|
                    |R|N|B|Q|K| | |R|
                """.trimIndent(), turn = Color.Light, allowCastle = true)
                val kingSquare = board.kingSquare(Light)

                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldContain(Move.Castle.Kingside(Light))
            }

            it("should not kingside castle if the rookMove has moved before") {
                val board = Snapshot.parse("""
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p|p|p|p|p|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |P|P|P|P|P|P|P|P|
                    |R|N|B|Q|K| | |R|
                """.trimIndent(), turn = Color.Light, allowCastle = true)
                    .updated(Move.parse("h1g1")!!)
                    .updated(Move.parse("e7e5")!!)
                    .updated(Move.parse("g1h1")!!)

                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.Castle.Kingside }
            }

            it("should not kingside castle if moving through check") {
                val board = Snapshot.parse("""
                    |r|n|b|q|k| |n|r|
                    |p|p|p|p|p|p|p|p|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | |b| | | | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R|N|B|Q|K| | |R|
                """.trimIndent(), turn = Color.Light, allowCastle = true)
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.Castle.Kingside }
            }

            it("should be able to queenside castle") {
                val board = Snapshot.parse("""
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p|p|p|p|p|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |P|P|P|P|P|P|P|P|
                    |R| | | |K|B|N|R|
                """.trimIndent(), turn = Color.Light, allowCastle = true)
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldContain(Move.Castle.Queenside(Light))
            }

            it("should not queenside castle if the rookMove has moved before") {
                val board = Snapshot.parse("""
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p|p|p|p|p|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |P|P|P|P|P|P|P|P|
                    |R| | | |K|B|N|R|
                """.trimIndent(), turn = Color.Light, allowCastle = true)
                    .updated(Move.parse("a1c1")!!)
                    .updated(Move.parse("e7e5")!!)
                    .updated(Move.parse("c1a1")!!)

                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.Castle.Queenside }
            }

            it("should not queenside castle if moving through check") {
                val board = Snapshot.parse("""
                    | |n|b|q|k|b|n|r|
                    |p|p|p|p|p|p|p|p|
                    | | | |r| | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |P|P| | |P|P|P|P|
                    |R| | | |K|B|N|R|
                """.trimIndent(), turn = Color.Light, allowCastle = true)
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.Castle.Queenside }
            }

            it("should not castle either way if the king has moved before") {
                val board = Snapshot.parse("""
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p|p|p|p|p|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |P|P|P|P|P|P|P|P|
                    |R| | | |K| | |R|
                """.trimIndent(), turn = Color.Light, allowCastle = true)
                    .updated(Move.parse("e1d1")!!)
                    .updated(Move.parse("e7e5")!!)
                    .updated(Move.parse("d1e1")!!)

                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.Castle }
            }

            it("should not castle if the king is blocked by his own allPieces") {
                val board = Snapshot.parse("""
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p|p|p|p|p|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |P|P|P|P|P|P|P|P|
                    |R|N|B|Q|K|B|N|R|
                """.trimIndent(), turn = Color.Light, allowCastle = true)

                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.Castle }
            }

            it("should not be allowed to castle to get out of check") {
                val board = Snapshot.parse("""
                    | |n|b|q|k|b|n|r|
                    |p|p|p|p|p|p|p|p|
                    | | | | |r| | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R| | | |K| | |R|
                """.trimIndent(), turn = Color.Light, allowCastle = true)
                val kingSquare = board.kingSquare(Light)
                val result = KingCalculator.potentialMoves(Light, kingSquare, board)

                result.shouldNotContain { it is Move.Castle }
            }
        }
    }
}
