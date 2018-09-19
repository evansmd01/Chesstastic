package chesstastic.engine.calculators

import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color
import chesstastic.engine.entities.PieceKind
import chesstastic.engine.entities.Square

interface AttackCalculator {
    fun timesSquareIsAttacked(target: Square, attacker: Color, board: Board): Int

    companion object {
        val all: Iterable<AttackCalculator> = listOf(
            PawnCalculator,
            KingCalculator,
            KnightCalculator,
            DiagonalAttackCalculator { it in listOf(PieceKind.Queen, PieceKind.Bishop) },
            HorizontalAttackCalculator { it in listOf(PieceKind.Queen, PieceKind.Rook) }
        )
    }
}
