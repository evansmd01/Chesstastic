package chesstastic.engine.entities

sealed class Piece(val color: Color) {
    override fun equals(other: Any?): Boolean = other?.hashCode() == hashCode()

    override fun hashCode(): Int = toString().hashCode()

    override fun toString(): String = color.toString() + this::class.simpleName
}

class Pawn(color: Color): Piece(color)
class Rook(color: Color): Piece(color)
class Bishop(color: Color): Piece(color)
class Knight(color: Color): Piece(color)
class Queen(color: Color): Piece(color)
class King(color: Color): Piece(color)
