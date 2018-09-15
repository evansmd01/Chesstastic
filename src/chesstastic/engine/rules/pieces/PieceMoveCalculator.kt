package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*

interface PieceMoveCalculator {
    /**
     * Lists any coordinate to which the piece could potentially move
     * Regardless of whether the coordinate is itself under attack
     * And regardless of whether the coordinate is occupied by a friendly piece.
     *
     * Basically, this is any coordinate to which the enemy king would not be allowed to move
     */
    val coordinatesUnderAttack: Iterable<Coordinate>

    /**
     * Any move to a coordinate that is not occupied by a friendly piece
     * does not put your king in check, and does not pass your king through check
     */
    val legalMoves: Iterable<Move>

    companion object {
        fun new(piece: Piece, coord: Coordinate, board: Board): PieceMoveCalculator = when(piece) {
            is Pawn -> PawnMoveCalculator(piece, coord, board)
            is Rook -> RookMoveCalculator(piece, coord, board)
            is Knight -> KnightMoveCalculator(piece, coord, board)
            is Bishop -> BishopMoveCalculator(piece, coord, board)
            is Queen -> QueenMoveCalculator(piece, coord, board)
            is King -> KingMoveCalculator(piece, coord, board)
        }
    }
}

