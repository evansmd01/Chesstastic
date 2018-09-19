package chesstastic.engine.calculators

import chesstastic.engine.entities.*

object QueenMoveCalculator: MoveCalculator {
    private val isQueen: (PieceKind) -> Boolean = { kind -> kind == PieceKind.Queen}
    val horizontal = HorizontalMoveCalculator(isQueen)
    val diagonal = DiagonalMoveCalculator(isQueen)

    override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> =
        diagonal.potentialMoves(color, fromSquare, board) + horizontal.potentialMoves(color, fromSquare, board)
}
