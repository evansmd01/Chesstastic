package chesstastic.engine.position.calculators

import chesstastic.engine.entities.*

object KnightCalculator: MoveCalculator, AttackCalculator {
    override fun attackers(target: Square, attacker: Color, board: Board): List<Pair<Piece,Square>> =
        squaresInRange(target)
            .mapNotNull { square ->
                val piece = board[square]
                if(piece?.kind == PieceKind.Knight && piece.color == attacker)
                    piece to square
                else null
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
