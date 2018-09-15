package chesstastic.engine.entities

sealed class Move(val from: Coordinate, val to: Coordinate) {
    override fun equals(other: Any?): Boolean =
        other is Move && other.from == from && other.to == to

    override fun hashCode(): Int = from.hashCode() + to.hashCode()
}

class BasicMove(from: Coordinate, to: Coordinate): Move(from, to)
class EnPassantMove(from: Coordinate, to: Coordinate, val captured: Coordinate): Move(from, to)
class CastleMove(from: Coordinate, to: Coordinate, val rookMove: BasicMove): Move(from, to)

