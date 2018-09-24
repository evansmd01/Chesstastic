package chesstastic.ai.heuristics

import chesstastic.ai.Constants
import chesstastic.engine.entities.Board

interface Heuristic {
    fun evaluate(board: Board): Score

    companion object {
        val factories = listOf<(Constants) -> Heuristic>(
            { Material(it) }
        )
    }
}


