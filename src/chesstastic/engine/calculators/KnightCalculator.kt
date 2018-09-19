package chesstastic.engine.calculators

import chesstastic.engine.entities.*

object KnightCalculator: MoveCalculator, AttackCalculator {
    override fun timesSquareIsAttacked(target: Square, attacker: Color, board: Board): Int =
        squaresInRange(target)
            .count {
                val piece = board[it]
                piece?.kind == PieceKind.Knight && piece.color == attacker
            }

    override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> =
        squaresInRange(fromSquare)
            .filterNot { board.isOccupiedByColor(it, color) }
            .map { Move.Basic(fromSquare, it) }

    private fun squaresInRange(fromSquare: Square): Iterable<Square> = listOfNotNull(
        fromSquare.transform(2, -1),
        fromSquare.transform(2, 1),
        fromSquare.transform(-2, -1),
        fromSquare.transform(-2, 1),
        fromSquare.transform(1, -2),
        fromSquare.transform(-1, -2),
        fromSquare.transform(1, 2),
        fromSquare.transform(-1, 2)
    )
}
