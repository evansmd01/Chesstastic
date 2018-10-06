package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.ai.models.Score
import chesstastic.engine.entities.*

class Development(override val weights: Weights): Heuristic {
    override val key = DEVELOPMENT

    override fun calculateBaseScore(board: Board): Score {
        var light = 0.0
        var dark = 0.0

        light += board.metadata.lightPlayer.knights
            .count { it.square !in knightSquares(Color.Light) } * weights[KNIGHT_DEVELOPMENT_BONUS]
        dark += board.metadata.darkPlayer.knights
            .count { it.square !in knightSquares(Color.Dark) } * weights[KNIGHT_DEVELOPMENT_BONUS]

        light += board.metadata.lightPlayer.bishops
            .count { it.square !in bishopSquares(Color.Light) } * weights[BISHOP_DEVELOPMENT_BONUS]
        dark += board.metadata.darkPlayer.bishops
            .count { it.square !in bishopSquares(Color.Dark) } * weights[BISHOP_DEVELOPMENT_BONUS]

        light += board.metadata.lightPlayer.rooks
            .count { it.square !in rookSquares(Color.Light) } * weights[ROOK_DEVELOPMENT_BONUS]
        dark += board.metadata.darkPlayer.rooks
            .count { it.square !in rookSquares(Color.Dark) } * weights[ROOK_DEVELOPMENT_BONUS]

        return Score(light, dark)
    }

    private fun startingRank(color: Color): Rank = when (color) {
        Color.Light -> Rank._1
        Color.Dark -> Rank._8
    }

    private fun knightSquares(color: Color): Set<Square> =
        with(startingRank(color)) { setOf(Square(File.B, this), Square(File.G, this)) }

    private fun bishopSquares(color: Color): Set<Square> =
        with(startingRank(color)) { setOf(Square(File.C, this), Square(File.F, this)) }

    private fun rookSquares(color: Color): Set<Square> =
        with(startingRank(color)) { setOf(Square(File.A, this), Square(File.H, this)) }

}
