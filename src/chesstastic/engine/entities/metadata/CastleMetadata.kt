package chesstastic.engine.entities.metadata

import chesstastic.engine.entities.File
import chesstastic.engine.entities.Rank
import chesstastic.engine.entities.Square

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
                Square(File.E, Rank._1),
                Square(File.H, Rank._1),
                Square(File.A, Rank._1),
                setOf(Square(File.F, Rank._1), Square(File.G, Rank._1)),
                setOf(Square(File.D, Rank._1), Square(File.C, Rank._1))
            )
            val DARK = CastleSquares(
                Square(File.E, Rank._8),
                Square(File.H, Rank._8),
                Square(File.A, Rank._8),
                setOf(Square(File.F, Rank._8), Square(File.G, Rank._8)),
                setOf(Square(File.D, Rank._8), Square(File.C, Rank._8))
            )
        }
    }
}
