package chesstastic.engine.entities

sealed class Move(val from: Coordinate, val to: Coordinate)

class BasicMove(from: Coordinate, to: Coordinate): Move(from, to)
class EnPassantMove(from: Coordinate, to: Coordinate, val captured: Coordinate): Move(from, to)
class CastleMove(from: Coordinate, to: Coordinate, val rookMove: BasicMove): Move(from, to)
class PawnPromotionMove(from: Coordinate, to: Coordinate, val preference: Piece): Move(from, to) {
    val withKnight by lazy { PawnPromotionMove(from, to, Knight(preference.color)) }
    val withQueen by lazy { PawnPromotionMove(from, to, Queen(preference.color)) }
}

