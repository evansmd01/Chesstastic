package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*
import kotlin.reflect.KClass

interface PieceMoveCalculator {
    fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move>

    fun timesSquareIsAttacked(target: Square, attacker: Color, board: Board): Int

    companion object {
        private val calculators = mapOf(
            Pawn::class to PawnMoveCalculator
        )

        val all: Iterable<PieceMoveCalculator> = calculators.values

        fun getBy(piece: Piece): PieceMoveCalculator = calculators[piece::class] ?: UnimplementedCalculator
    }
}

class UnimplementedCalculator {
    companion object: PieceMoveCalculator {
        override fun timesSquareIsAttacked(target: Square, attacker: Color, board: Board): Int = 0
        override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> = listOf()
    }
}
