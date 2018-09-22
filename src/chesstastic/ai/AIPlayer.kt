package chesstastic.ai

import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Move

interface AIPlayer {
    fun selectMove(board: Board): Move
}
