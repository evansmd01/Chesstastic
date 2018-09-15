package chestastic.engine

sealed class Piece(val color: Color)

class Pawn(color: Color): Piece(color)
class Rook(color: Color): Piece(color)
class Bishop(color: Color): Piece(color)
class Knight(color: Color): Piece(color)
class Queen(color: Color): Piece(color)
class King(color: Color): Piece(color)
