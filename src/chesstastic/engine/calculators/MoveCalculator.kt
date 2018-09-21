package chesstastic.engine.calculators

import chesstastic.engine.entities.*
import chesstastic.engine.entities.PieceKind.*

interface MoveCalculator {
    fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move>

    operator fun plus(other: MoveCalculator): MoveCalculator {
        val self = this
        return object : MoveCalculator {
            override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> =
                self.potentialMoves(color, fromSquare, board) + other.potentialMoves(color, fromSquare, board)
        }
    }

    companion object {
        private val calculators = mapOf(
            Pawn to PawnCalculator,
            Queen to HorizontalCalculator { it == Queen } + DiagonalCalculator { it == Queen },
            Knight to KnightCalculator,
            Bishop to DiagonalCalculator { it == Bishop },
            Rook to HorizontalCalculator { it == Rook },
            King to KingCalculator
        )

        val all: Iterable<MoveCalculator> = calculators.values

        fun getBy(piece: PieceKind): MoveCalculator = calculators[piece]
            ?: throw NotImplementedError("No calculator for $piece")
    }
}
