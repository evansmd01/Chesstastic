package chesstastic.ai

import chesstastic.ai.models.PositionEvaluation
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Move
import chesstastic.tests.framework.ChessTestSuite

@Suppress("unused")
class EvaluationTreeTests: ChessTestSuite() {
    init {
        describe("EvaluationTree") {
            it("calculates leaf nodes correctly") {
                val board = Board()
                val mockEvaluation = PositionEvaluation(
                    null, false, emptyList()
                )
                val tree = EvaluationTree.new(board, mockEvaluation)

                // add some non-terminating root nodes
                tree.addChild(EvaluationTree.BranchingNode(
                    board, mockEvaluation, Move.parse("e2e4")!!, Move.parse("e7e5")!!
                ))
                tree.addChild(EvaluationTree.BranchingNode(
                    board, mockEvaluation, Move.parse("e2e3")!!, Move.parse("e7e5")!!
                ))

                // add a terminating root nodes
                tree.addChild(EvaluationTree.TerminatingNode(
                    board, mockEvaluation, Move.parse("a2a3")!!
                ))

                val appendableNodes = tree.appendableLeafNodes()
                appendableNodes.size.shouldBe(2)

                // add a non-terminating node off an existing leaf
                appendableNodes.first().addChild(EvaluationTree.BranchingNode(
                    board, mockEvaluation, Move.parse("h2h3")!!, Move.parse("h7h6")!!
                ))
                // add a terminating node off an exisitng leaf
                appendableNodes.first().addChild(EvaluationTree.TerminatingNode(
                    board, mockEvaluation, Move.parse("g2g3")!!
                ))

                // started with 5 leaves, then branched two more leaves off of one of the existing ones,
                // making that original one no longer a leaf.
                //      3 leaves - 1 existing leaf + 2 new leafs = 4
                tree.leafNodes().size.shouldBe(4)
            }
        }
    }
}


