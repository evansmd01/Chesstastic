package chesstastic.engine.entities.metadata

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Color.*

/**
 * Cached contextual information inferred from history as the game progresses,
 * to prevent wasting cycles looping through history to check for conditions
 * over and over while evaluating thousands of board positions
 */
data class HistoryMetadata(
    val history: History,
    val currentTurn: Color,
    val lightCastleMetadata: CastleMetadata,
    val darkCastleMetadata: CastleMetadata,
    val inactivityCount: Int,
    val moveCount: Int
) {
    operator fun plus(moveData: MoveMetadata) =
        HistoryMetadata(
            history = History(moveData.move, history),
            currentTurn = currentTurn.opposite,
            lightCastleMetadata = lightCastleMetadata.updated(moveData.move.from),
            darkCastleMetadata = darkCastleMetadata.updated(moveData.move.from),
            inactivityCount = when {
                moveData.piece.kind == PieceKind.Pawn -> 0
                moveData.captured != null -> 0
                else -> inactivityCount + 1
            },
            moveCount = moveCount + 1
        )

    companion object {
        val EMPTY = HistoryMetadata(
            history = History(null, null),
            currentTurn = Light,
            lightCastleMetadata = CastleMetadata.LIGHT,
            darkCastleMetadata = CastleMetadata.DARK,
            inactivityCount = 0,
            moveCount = 0
        )
    }
}

