package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.ai.models.Score
import chesstastic.engine.entities.Board
import chesstastic.engine.metadata.PieceMetadata

class KingPressure(override val weights: Weights): Heuristic {
    override val key = KING_PRESSURE

    override fun calculateBaseScore(board: Board): Score {

        val lightSurroundings = analyzeSurroundings(board.metadata.lightPlayer.king, board)
        val dark = lightSurroundings.attackedSquareCount /
            lightSurroundings.squareCount * weights[KING_PRESSURE_ATTACK_RATIO_BONUS]

        val darkSurroundings = analyzeSurroundings(board.metadata.darkPlayer.king, board)
        val light = darkSurroundings.attackedSquareCount /
            darkSurroundings.squareCount * weights[KING_PRESSURE_ATTACK_RATIO_BONUS]

        return Score(light, dark)
    }

    private fun analyzeSurroundings(king: PieceMetadata, board: Board): SurroundingData {
        val surroundings = with(king.square) { listOfNotNull(
            this.transform(1, -1),
            this.transform(1, 0),
            this.transform(1, 1),
            this.transform(0, -1),
            this.transform(0, 1),
            this.transform(-1, -1),
            this.transform(-1, 0),
            this.transform(-1, 1)
        ) }.mapNotNull { board.metadata.squares[it] }

        return SurroundingData(
            squareCount = surroundings.count(),
            attackedSquareCount = surroundings.count { it.isAttackedBy.any{ atk -> atk.piece.color == king.piece.color.opposite} },
            rawAttackCount = surroundings.sumBy { it.isAttackedBy.count{ atk -> atk.piece.color == king.piece.color.opposite} }
        )
    }

    private data class SurroundingData(val squareCount: Int, val attackedSquareCount: Int, val rawAttackCount: Int)
}
