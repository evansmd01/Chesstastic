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
    val lightCastling: CastleMetadata,
    val darkCastling: CastleMetadata,
    val inactivityCount: Int,
    val moveCount: Int
) {
    operator fun plus(moveData: MoveMetadata): HistoryMetadata {
        var newLightCastle = lightCastling
        var newDarkCastle = darkCastling
        if(moveData.move is Move.Castle) when (moveData.piece.color) {
            Light -> newLightCastle = lightCastling.copy(castled = moveData.move, kingHasMoved = true)
            Dark -> newDarkCastle = darkCastling.copy(castled = moveData.move, kingHasMoved = true)
        } else {
            newLightCastle = lightCastling.updated(
                if (currentTurn == Light) moveData.move.from else moveData.move.to
            )
            newDarkCastle = darkCastling.updated(
                if (currentTurn == Dark) moveData.move.from else moveData.move.to
            )
        }

        return HistoryMetadata(
            history = History(moveData.move, history),
            currentTurn = currentTurn.opposite,
            lightCastling = newLightCastle,
            darkCastling = newDarkCastle,
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
            lightCastling = CastleMetadata.LIGHT,
            darkCastling = CastleMetadata.DARK,
            inactivityCount = 0,
            moveCount = 0
        )
    }
}
