package chesstastic.test

import chesstastic.test.engine.entities.*
import chesstastic.test.engine.calculators.*
import chesstastic.test.engine.calculators.moves.*
import chesstastic.test.engine.calculators.moves.pieces.*
import chesstastic.test.framework.ChessTestFramework

class ChessTests {
    companion object {
        private val suites = listOf(
            { MoveTests() },
            { BoardTests() },
            { BoardCalculatorTests() },
            { PawnMoveCalculatorTests() },
            { RookMoveCalculatorTests() },
            { KnightMoveCalculatorTests() },
            { BishopMoveCalculatorTests() },
            { QueenMoveCalculatorTests() },
            { KingMoveCalculatorTests() }
        )

        fun run() {
            ChessTestFramework.execute(suites)
        }
    }
}


