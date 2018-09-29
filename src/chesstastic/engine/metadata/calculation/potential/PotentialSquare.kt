package chesstastic.engine.metadata.calculation.potential

import chesstastic.engine.entities.Color
import chesstastic.engine.entities.Piece
import chesstastic.engine.entities.Square
import chesstastic.engine.metadata.PieceMetadata
import chesstastic.engine.metadata.PinMetadata
import chesstastic.engine.metadata.SkewerMetadata
import chesstastic.engine.metadata.SquareMetadata

data class PotentialSquare(
    val square: Square,
    val occupant: Piece?,
    val isAttackedBy: MutableSet<PieceMetadata>,
    val isSupportedBy: MutableSet<PieceMetadata>,
    val pins: MutableSet<PinMetadata>,
    val skewers: MutableSet<SkewerMetadata>
) {
    fun finalize() = SquareMetadata(
        square,
        occupant,
        isAttackedBy = isAttackedBy,
        isSupportedBy = isSupportedBy,
        pins = pins,
        skewers = skewers
    )

    fun attackedBy(color: Color) = isAttackedBy.filter { it.piece.color == color }

    companion object {
        fun from(square: Square, piece: Piece?) = PotentialSquare(
            square,
            piece,
            mutableSetOf(),
            mutableSetOf(),
            mutableSetOf(),
            mutableSetOf()
        )
    }
}
