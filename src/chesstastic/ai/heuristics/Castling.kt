package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.ai.models.Score
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Move

class Castling(override val weights: Weights): Heuristic {
    override val key = CASTLING

    override fun calculateBaseScore(board: Board): Score {
        var light = 0.0
        var dark = 0.0

        val lightCastle = board.historyMetadata.lightCastling.castled
        val darkCastle = board.historyMetadata.darkCastling.castled

        when  {
            lightCastle is Move.Castle.Queenside ->
                light += weights[QUEENSIDE_CASTLE_BONUS]
            lightCastle is Move.Castle.Kingside ->
                light += weights[KINGSIDE_CASTLE_BONUS]
            board.metadata.lightPlayer.moves.kingMoves.none { it.move is Move.Castle } ->
                dark += weights[CANNOT_CASTLE_PENALTY]
        }
        when  {
            darkCastle is Move.Castle.Queenside ->
                dark += weights[QUEENSIDE_CASTLE_BONUS]
            darkCastle is Move.Castle.Kingside ->
                dark += weights[KINGSIDE_CASTLE_BONUS]
            board.metadata.darkPlayer.moves.kingMoves.none { it.move is Move.Castle } ->
                light += weights[CANNOT_CASTLE_PENALTY]
        }

        return Score(light, dark)
    }
}

