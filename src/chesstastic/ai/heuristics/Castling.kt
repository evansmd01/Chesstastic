package chesstastic.ai.heuristics

import chesstastic.ai.Constants
import chesstastic.ai.Constants.Key.*
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Move

class Castling(override val constants: Constants): Heuristic {
    override val key = CASTLING

    override fun calculateBaseScore(board: Board): Score {
        var light = 0.0
        var dark = 0.0

        val lightCastle = board.historyMetadata.lightCastleMetadata.castled
        val darkCastle = board.historyMetadata.darkCastleMetadata.castled

        when  {
            lightCastle is Move.Castle.Queenside ->
                light += constants[QUEENSIDE_CASTLE_BONUS]
            lightCastle is Move.Castle.Kingside ->
                light += constants[KINGSIDE_CASTLE_BONUS]
            board.metadata.lightPlayer.moves.kingMoves.none { it.move is Move.Castle } ->
                dark += constants[CANNOT_CASTLE_PENALTY]
        }
        when  {
            darkCastle is Move.Castle.Queenside ->
                dark += constants[QUEENSIDE_CASTLE_BONUS]
            darkCastle is Move.Castle.Kingside ->
                dark += constants[KINGSIDE_CASTLE_BONUS]
            board.metadata.darkPlayer.moves.kingMoves.none { it.move is Move.Castle } ->
                light += constants[CANNOT_CASTLE_PENALTY]
        }

        return Score.fromImbalance(light, dark)
    }
}

