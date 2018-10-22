package chesstastic.ai

import chesstastic.engine.entities.Color
import chesstastic.tests.framework.ChessTestSuite
import chesstastic.util.Snapshot

@Suppress("unused")
class EvaluationTests: ChessTestSuite() {
    init {
        describe("evaluate") {
            describe("All else being even") {
                it("should be bad to leave your queen hanging") {
                    val board = Snapshot.parse("""
                        |r|n|b|q|k|b| |r|
                        |p|p|p|p|p|p|p|p|
                        | | | | | |n| | |
                        | | | | | | | |Q|
                        | | | | |P| | | |
                        | | | | | | | | |
                        |P|P|P|P| |P|P|P|
                        |R|N|B| |K|B|N|R|
                    """.trimIndent(), turn = Color.Dark)

                    val eval = Chesstastic.DEFAULT.evaluate(board)

                    eval.score.dark.shouldBeGreaterThan(eval.score.light)
                }

                it("should be bad to be losing the exchange") {
                    val board = Snapshot.parse("""
                        |r| |b|q|k| | |r|
                        |p|p|p|p|p|p|p|p|
                        | |b|n| | | | | |
                        | | | | |n| | | |
                        | | | | | | | | |
                        | | |B| | |N| | |
                        |P|P|P|P|P|P|P|P|
                        |R|N|B|Q|K| | |R|
                    """.trimIndent(), turn = Color.Light)

                    val eval = Chesstastic.DEFAULT.evaluate(board)

                    eval.score.light.shouldBeGreaterThan(eval.score.dark)
                }
            }
        }
    }
}
