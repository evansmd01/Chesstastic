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
            lightCastleMetadata = lightCastleMetadata.updated(moveData.move.from),
            darkCastleMetadata = darkCastleMetadata.updated(moveData.move.from),
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
            lightCastleMetadata = CastleMetadata.LIGHT,
            darkCastleMetadata = CastleMetadata.DARK,
            inactivityCount = 0,
            moveCount = 0
        )
    }

    data class CastleMetadata(
        val kingHasMoved: Boolean,
        val kingsideRookHasMoved: Boolean,
        val queensideRookHasMoved: Boolean,
        val squares: CastleSquares
    ) {
        fun updated(movedFrom: Square): CastleMetadata {
            return when {
                !kingHasMoved && movedFrom == squares.king ->
                    copy(kingHasMoved = true)
                !kingsideRookHasMoved && movedFrom == squares.kingsideRook ->
                    copy(kingsideRookHasMoved = true)
                !queensideRookHasMoved && movedFrom == squares.queensideRook ->
                    copy(queensideRookHasMoved = true)
                else -> this
            }
        }

        companion object {
            val LIGHT = CastleMetadata(false, false, false, CastleSquares.LIGHT)
            val DARK = CastleMetadata(false, false, false, CastleSquares.DARK)
        }

        data class CastleSquares(
            val king: Square,
            val kingsideRook: Square,
            val queensideRook: Square,
            val kingsidePassing: Set<Square>,
            val queensidePassing: Set<Square>) {
            companion object {
                val LIGHT = CastleSquares(
                    Square(E, _1),
                    Square(H, _1),
                    Square(A, _1),
                    setOf(Square(F, _1), Square(G, _1)),
                    setOf(Square(D, _1), Square(C, _1))
                )
                val DARK = CastleSquares(
                    Square(E, _8),
                    Square(H, _8),
                    Square(A, _8),
                    setOf(Square(F, _8), Square(G, _8)),
                    setOf(Square(D, _8), Square(C, _8))
                )
            }
        }
    }
}
