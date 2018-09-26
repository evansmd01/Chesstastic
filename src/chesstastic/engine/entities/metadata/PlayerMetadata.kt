package chesstastic.engine.entities.metadata

import chesstastic.engine.entities.Color
import chesstastic.engine.entities.Move

/**
 * Contextual information about one of the players' positions
 */
data class PlayerMetadata(
    val color: Color,
    val pieces: Set<PieceMetadata>,
    val pawns: Set<PieceMetadata>,
    val rooks: Set<PieceMetadata>,
    val knights: Set<PieceMetadata>,
    val bishops: Set<PieceMetadata>,
    val queens: Set<PieceMetadata>,
    val king: PieceMetadata,
    val moves: Set<Move> = emptySet()
) {
    val horizontalPieces = queens + rooks
    val diagonalPieces = queens + bishops
}
