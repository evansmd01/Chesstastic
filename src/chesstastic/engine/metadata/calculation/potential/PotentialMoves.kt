package chesstastic.engine.metadata.calculation.potential

import chesstastic.engine.entities.PieceKind
import chesstastic.engine.metadata.MoveMetadata
import chesstastic.engine.metadata.PinMetadata
import chesstastic.engine.metadata.PlayerMetadata
import chesstastic.engine.metadata.SkewerMetadata

data class PotentialMoves(
    val pawnMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val bishopMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val knightMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val rookMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val queenMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val kingMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val pins: MutableSet<PinMetadata> = mutableSetOf(),
    val skewers: MutableSet<SkewerMetadata> = mutableSetOf()
) {
    fun movesFor(kind: PieceKind): MutableSet<MoveMetadata> = when (kind) {
        PieceKind.Pawn -> pawnMoves
        PieceKind.Bishop -> bishopMoves
        PieceKind.Knight -> knightMoves
        PieceKind.Rook -> rookMoves
        PieceKind.Queen -> queenMoves
        PieceKind.King -> kingMoves
    }

    fun finalize() = PlayerMetadata.Moves(
        pawnMoves = pawnMoves,
        bishopMoves = bishopMoves,
        knightMoves = knightMoves,
        rookMoves = rookMoves,
        queenMoves = queenMoves,
        kingMoves = kingMoves,
        pins = pins,
        skewers = skewers
    )
}
