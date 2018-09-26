package chesstastic.engine.entities.metadata

import chesstastic.engine.entities.Piece
import chesstastic.engine.entities.Square

/**
 * contextual information about a square to be reused in various computations without wasting cycles recomputing
 */
data class SquareMetadata(
    val square: Square,
    val occupant: Piece?,
    val attackedBy: Set<PieceMetadata>,
    val supportedBy: Set<PieceMetadata>,
    val pins: Set<PinMetadata>,
    val skewers: Set<SkewerMetadata>
) {
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
