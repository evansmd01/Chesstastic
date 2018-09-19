package chesstastic.engine.entities

data class Piece(val kind: PieceKind, val color: Color)

enum class PieceKind {
    Pawn, Rook, Knight, Bishop, Queen, King
}
