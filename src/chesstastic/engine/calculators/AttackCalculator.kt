package chesstastic.engine.calculators

import chesstastic.engine.entities.*

// TODO: Toss out this interface & implementations after implementing BoardMetadata. Attacks will be kept track of while iterating through potential moves.
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
