package chesstastic.engine.entities.metadata

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.File.*

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
            lightCastleMetadata = lightCastleMetadata.updated(moveData.move.from, Light),
            darkCastleMetadata = darkCastleMetadata.updated(moveData.move.from, Dark),
            inactivityCount = when {
                moveData.pieceKind == PieceKind.Pawn -> 0
                moveData.captured != null -> 0
                else -> inactivityCount + 1
            },
            moveCount = moveCount + 1
        )

    companion object {
        val EMPTY = HistoryMetadata(
            history = History(null, null),
            currentTurn = Light,
            lightCastleMetadata = CastleMetadata.EMPTY,
            darkCastleMetadata = CastleMetadata.EMPTY,
            inactivityCount = 0,
            moveCount = 0
        )
    }

    data class CastleMetadata(
        val kingHasMoved: Boolean,
        val kingsideRookHasMoved: Boolean,
        val queensideRookHasMoved: Boolean
    ) {
        fun updated(movedFrom: Square, color: Color): CastleMetadata {
            val startSquares = if (color == Light) StartSquares.LIGHT else StartSquares.DARK
            return when {
                !kingHasMoved && movedFrom == startSquares.king ->
                    copy(kingHasMoved = true)
                !kingsideRookHasMoved && movedFrom == startSquares.kingsideRook ->
                    copy(kingsideRookHasMoved = true)
                !queensideRookHasMoved && movedFrom == startSquares.queensideRook ->
                    copy(queensideRookHasMoved = true)
                else -> this
            }
        }

        companion object {
            val EMPTY = CastleMetadata(false, false, false)
        }

        data class StartSquares(val king: Square, val kingsideRook: Square, val queensideRook: Square) {
            companion object {
                val LIGHT = StartSquares(Square(E, _1), Square(H, _1), Square(A, _1))
                val DARK = StartSquares(Square(E, _8), Square(H, _8), Square(A, _8))
            }
        }
    }
}
