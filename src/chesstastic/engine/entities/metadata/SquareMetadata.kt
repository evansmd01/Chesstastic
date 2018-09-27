package chesstastic.engine.entities.metadata

import chesstastic.engine.entities.Color
import chesstastic.engine.entities.Piece
import chesstastic.engine.entities.Square

/**
 * contextual information about a square to be reused in various computations without wasting cycles recomputing
 */
data class SquareMetadata(
    val square: Square,
    val occupant: Piece?,
    val isAttackedBy: Set<PieceMetadata>,
    val isSupportedBy: Set<PieceMetadata>,
    val pins: Set<PinMetadata>,
    val skewers: Set<SkewerMetadata>
) {
    private val groupedByColor = isAttackedBy.groupBy { it.piece.color }
    fun attackedBy(color: Color) = groupedByColor[color] ?: emptyList()

    companion object {
        fun from(square: Square, piece: Piece?) = SquareMetadata(
            square,
            piece,
            emptySet(),
            emptySet(),
            emptySet(),
            emptySet()
        )
    }
}
