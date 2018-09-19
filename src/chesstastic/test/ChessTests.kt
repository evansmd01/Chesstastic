package chesstastic.test

import chesstastic.test.engine.entities.*
import chesstastic.test.engine.calculators.*
import chesstastic.test.framework.ChessTestFramework

class ChessTests {
    companion object {
        private val suites = listOf(
            { MoveTests() },
            { BoardTests() },
            { BoardCalculatorTests() },
            { PawnCalculatorTests() },
            { KingCalculatorTests() }
        )

        fun run() {
            ChessTestFramework.execute(suites)
        }
    }
}


