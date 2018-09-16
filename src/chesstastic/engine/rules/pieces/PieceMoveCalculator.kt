package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*

interface PieceMoveCalculator {
    fun legalMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move>

    companion object {
        fun new(piece: Piece) = when(piece) {
            is Pawn -> PawnMoveCalculator
            else -> UnimplementedCalculator
        }
    }
}

class UnimplementedCalculator {
    companion object: PieceMoveCalculator {
        override fun legalMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> = listOf()
    }
}
