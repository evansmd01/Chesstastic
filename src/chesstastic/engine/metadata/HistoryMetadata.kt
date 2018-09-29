package chesstastic.engine.metadata

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
    operator fun plus(moveData: MoveMetadata): HistoryMetadata {
        var newLightCastle = lightCastleMetadata
        var newDarkCastle = darkCastleMetadata
        if(moveData.move is Move.Castle) when (moveData.piece.color) {
            Light -> newLightCastle = lightCastleMetadata.copy(castled = moveData.move, kingHasMoved = true)
            Dark -> newDarkCastle = darkCastleMetadata.copy(castled = moveData.move, kingHasMoved = true)
        } else {
            newLightCastle = lightCastleMetadata.updated(
                if (currentTurn == Light) moveData.move.from else moveData.move.to
            )
            newDarkCastle = darkCastleMetadata.updated(
                if (currentTurn == Dark) moveData.move.from else moveData.move.to
            )
        }

        return HistoryMetadata(
            history = History(moveData.move, history),
            currentTurn = currentTurn.opposite,
            lightCastleMetadata = newLightCastle,
            darkCastleMetadata = newDarkCastle,
            inactivityCount = when {
                moveData.piece.kind == PieceKind.Pawn -> 0
                moveData.capturing != null -> 0
                else -> inactivityCount + 1
            },
            moveCount = moveCount + 1
        )
    }

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

