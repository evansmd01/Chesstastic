package chesstastic.ai

import chesstastic.ai.criteria.*
import chesstastic.ai.values.Score
import chesstastic.engine.entities.*

object ChesstasticAI {
    val criteria = listOf(
        MovesAvailable,
        Material
    )

    fun selectMove(
        board: Board,
        depth: Int,
        breadth: Int): Move =
        findBestMove(board.turn, board, depth, breadth)?.first ?:
        throw Error("Could not find a move.")

    private fun findBestMove(
        forColor: Color,
        currentBoard: Board,
        depth: Int,
        breadth: Int
    ): Pair<Move, Score>? {
        val evaluations: List<Triple<Move, Board, Score>> = currentBoard.legalMoves.shuffled()
            .asSequence()
            .map { move ->
                val updatedBoard = currentBoard.updated(move)
                Triple(move, updatedBoard, evaluate(updatedBoard))
            }
            .sortedByDescending { (_, _, score) ->
                score.ratioInFavorOf(currentBoard.turn)
            }
            .toList()

        return if(depth < 1 && currentBoard.turn == forColor) {
            val best = evaluations.first()
            Pair(best.first, best.third)
        } else {
            val best = evaluations.asSequence().take(breadth)
                .mapNotNull { (originalMove, updatedBoard, currentScore) ->
                    when {
                        updatedBoard.isCheckmate ->
                            Triple(originalMove, updatedBoard, Score.checkmate(updatedBoard.turn))
                        updatedBoard.isStalemate ->
                            Triple(originalMove, updatedBoard, Score.stalemate(currentBoard.turn, currentScore))
                        else -> {
                            val best = findBestMove(forColor, updatedBoard, depth - 1, breadth)
                            best?.let { Triple(originalMove, updatedBoard, best.second) }
                        }
                    }
                }.sortedByDescending { (_, updatedBoard, potentialScore) ->
                    potentialScore.ratioInFavorOf(updatedBoard.turn)
                }.firstOrNull()
            best?.let { Pair(it.first, it.third)}
        }
    }

    private fun evaluate(board: Board): Score =
        criteria.asSequence().map { it.evaluate(board) }
            .fold(Score.even) { total, score -> total + score }
}


