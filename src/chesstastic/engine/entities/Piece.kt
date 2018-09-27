package chesstastic.engine.entities

data class Piece(val kind: PieceKind, val color: Color)

/**
 * Types of allPieces, in ordinal order from least to most important
 */
enum class PieceKind {
    Pawn,
    Knight,
    Bishop,
    Rook,
    Queen,
    King
}
