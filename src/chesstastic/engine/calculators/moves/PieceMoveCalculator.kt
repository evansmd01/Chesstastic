package chesstastic.engine.calculators.moves

import chesstastic.engine.calculators.moves.pieces.*
import chesstastic.engine.entities.*

interface PieceMoveCalculator {
    fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move>

    fun timesSquareIsAttacked(target: Square, attacker: Color, board: Board): Int

    companion object {
        private val calculators = mapOf(
            Pawn::class to PawnMoveCalculator,
            Queen::class to QueenMoveCalculator,
            Knight::class to KnightMoveCalculator,
            Bishop::class to BishopMoveCalculator,
            Rook::class to RookMoveCalculator,
            King::class to KingMoveCalculator
        )

        val all: Iterable<PieceMoveCalculator> = calculators.values

        fun getBy(piece: Piece): PieceMoveCalculator = calculators[piece::class] ?: throw NotImplementedError("No calculator for ${piece::class.simpleName}")
    }
}
