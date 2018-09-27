package chesstastic.engine.metadata

import chesstastic.engine.entities.Color

/**
 * Contextual information about one of the players' positions
 */
data class PlayerMetadata(
    val color: Color,
    val allPieces: Set<PieceMetadata>,
    val pawns: Set<PieceMetadata>,
    val rooks: Set<PieceMetadata>,
    val knights: Set<PieceMetadata>,
    val bishops: Set<PieceMetadata>,
    val queens: Set<PieceMetadata>,
    val king: PieceMetadata,
    val moves: Moves = Moves.EMPTY
) {
    val rooksAndQueens = rooks + queens
    val bishopsAndQueens = bishops + queens

    data class Moves(
        val pawnMoves: Set<MoveMetadata>,
        val bishopMoves: Set<MoveMetadata>,
        val knightMoves: Set<MoveMetadata>,
        val rookMoves: Set<MoveMetadata>,
        val queenMoves: Set<MoveMetadata>,
        val kingMoves: Set<MoveMetadata>,
        val pins: Set<PinMetadata>,
        val skewers: Set<SkewerMetadata>
    ) {
        val all by lazy {
            pawnMoves + bishopMoves + knightMoves + rookMoves + queenMoves + kingMoves
        }

        companion object {
            val EMPTY = Moves(
                emptySet(),
                emptySet(),
                emptySet(),
                emptySet(),
                emptySet(),
                emptySet(),
                emptySet(),
                emptySet()
            )
        }
    }
}
