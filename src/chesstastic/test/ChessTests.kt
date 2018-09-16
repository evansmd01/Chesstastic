package chesstastic.test

import chesstastic.test.engine.entities.MoveTests
import chesstastic.test.framework.ChessTestFramework

class ChessTests {
    companion object {
        private val suites = listOf(
            { MoveTests() }
        )

        fun run() {
            MoveTests::class.constructors.first()
            ChessTestFramework.execute(suites)
        }
    }
}


