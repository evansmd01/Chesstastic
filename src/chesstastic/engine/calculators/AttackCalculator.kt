package chesstastic.engine.calculators

import chesstastic.engine.entities.*

interface AttackCalculator {
    fun attackers(target: Square, attacker: Color, board: Board): List<Pair<Piece,Square>>

    companion object {
        val all: List<AttackCalculator> = listOf(
            PawnCalculator,
            KingCalculator,
            KnightCalculator,
            DiagonalCalculator { it in listOf(PieceKind.Queen, PieceKind.Bishop) },
            HorizontalCalculator { it in listOf(PieceKind.Queen, PieceKind.Rook) }
        )
    }
}
