package chesstastic.test

import chesstastic.test.engine.entities.*
import chesstastic.test.engine.rules.*
import chesstastic.test.engine.rules.pieces.*
import chesstastic.test.framework.ChessTestFramework

class ChessTests {
    companion object {
        private val suites = listOf(
            { MoveTests() },
            { BoardTests() },
            { MoveCalculatorTests() },
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


