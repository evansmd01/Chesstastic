package chesstastic.engine.metadata

import chesstastic.engine.entities.File.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.*

data class CastleMetadata(
    val kingHasMoved: Boolean,
    val kingsideRookMovedOrCaptured: Boolean,
    val queensideRookMovedOrCaptured: Boolean,
    val squares: CastleSquares,
    val castled: Move.Castle?
) {
    fun updated(removedFrom: Square): CastleMetadata {
        return when {
            !kingHasMoved && removedFrom == squares.king ->
                copy(kingHasMoved = true)
            !kingsideRookMovedOrCaptured && removedFrom == squares.kingsideRook ->
                copy(kingsideRookMovedOrCaptured = true)
            !queensideRookMovedOrCaptured && removedFrom == squares.queensideRook ->
                copy(queensideRookMovedOrCaptured = true)
            else -> this
        }
    }

    companion object {
        val LIGHT = CastleMetadata(false, false, false, CastleSquares.LIGHT, null)
        val DARK = CastleMetadata(false, false, false, CastleSquares.DARK, null)
    }

    data class CastleSquares(
        val king: Square,
        val kingsideRook: Square,
        val queensideRook: Square,
        val kingsidePassingCheck: Set<Square>,
        val queensidePassingCheck: Set<Square>,
        private val queensideExtraBlockingSquare: Square
    ) {
        val kingsideBlocking = kingsidePassingCheck
        val queensideBlocking = queensidePassingCheck + queensideExtraBlockingSquare

        companion object {
            val LIGHT = CastleSquares(
                Square(E, _1),
                Square(H, _1),
                Square(A, _1),
                setOf(Square(F, _1), Square(G, _1)),
                setOf(Square(D, _1), Square(C, _1)),
                Square(B, _1)
            )
            val DARK = CastleSquares(
                Square(E, _8),
                Square(H, _8),
                Square(A, _8),
                setOf(Square(F, _8), Square(G, _8)),
                setOf(Square(D, _8), Square(C, _8)),
                Square(B, _8)
            )
        }
    }
}
