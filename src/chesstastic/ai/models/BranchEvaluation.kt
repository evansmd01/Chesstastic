package chesstastic.ai.models

import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Move

data class BranchEvaluation(val branch: List<Move>, val score: Score, val board: Board)
