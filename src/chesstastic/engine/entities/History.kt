package chesstastic.engine.entities

data class History(val mostRecent: Move?, val previous: History?) {
    fun first(): Move? = if (previous?.mostRecent != null) previous.first() else mostRecent

    override fun toString(): String =
        mostRecent?.let { "${previous?.toString() ?: ""}$it " } ?: ""
}

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
            lightCastleMetadata = when (moveData.move.from) {
                LIGHT_KING -> lightCastleMetadata.copy(kingHasMoved = true)
                LIGHT_KINGSIDE_ROOK -> lightCastleMetadata.copy(kingsideRookHasMoved = true)
                LIGHT_QUEENSIDE_ROOK -> lightCastleMetadata.copy(queensideRookHasMoved = true)
                else -> lightCastleMetadata
            },
            darkCastleMetadata = when (moveData.move.from) {
                DARK_KING -> darkCastleMetadata.copy(kingHasMoved = true)
                DARK_KINGSIDE_ROOK -> darkCastleMetadata.copy(kingsideRookHasMoved = true)
                DARK_QUEENSIDE_ROOK -> darkCastleMetadata.copy(queensideRookHasMoved = true)
                else -> darkCastleMetadata
            },
            inactivityCount = when {
                moveData.pieceKind == PieceKind.Pawn -> 0
                moveData.captured != null -> 0
                else -> inactivityCount + 1
            },
            moveCount = moveCount + 1
        )

    companion object {
        val EMPTY = HistoryMetadata(
            History(null, null),
            Color.Light,
            CastleMetadata(false, false, false),
            CastleMetadata(false, false, false),
            0,
            0
        )

        private val LIGHT_KING = Square(File.E, Rank._1)
        private val LIGHT_KINGSIDE_ROOK = Square(File.H, Rank._1)
        private val LIGHT_QUEENSIDE_ROOK = Square(File.A, Rank._1)
        private val DARK_KING = Square(File.E, Rank._8)
        private val DARK_KINGSIDE_ROOK = Square(File.H, Rank._8)
        private val DARK_QUEENSIDE_ROOK = Square(File.A, Rank._8)
    }
}

data class CastleMetadata(
    val kingHasMoved: Boolean,
    val kingsideRookHasMoved: Boolean,
    val queensideRookHasMoved: Boolean
)
