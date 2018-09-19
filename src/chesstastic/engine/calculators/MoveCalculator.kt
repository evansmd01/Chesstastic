package chesstastic.engine.calculators

import chesstastic.engine.entities.*
import chesstastic.engine.entities.PieceKind.*

interface MoveCalculator {
    fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move>

    companion object {
        private val calculators = mapOf(
            Pawn to PawnCalculator,
            Queen to QueenMoveCalculator,
            Knight to KnightCalculator,
            Bishop to DiagonalMoveCalculator { it == Bishop },
            Rook to HorizontalMoveCalculator { it == Rook },
            King to KingCalculator
        )

        val all: Iterable<MoveCalculator> = calculators.values

        fun getBy(piece: PieceKind): MoveCalculator = calculators[piece] ?: throw NotImplementedError("No calculator for $piece")
    }
}
