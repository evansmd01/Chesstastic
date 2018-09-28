package chesstastic.engine.metadata.calculation.moves

import chesstastic.engine.entities.*
import chesstastic.engine.metadata.MoveMetadata
import chesstastic.engine.metadata.PieceMetadata

object KnightMoveCalculator {
    fun calculate(color: Color, fromSquare: Square, getPiece: (Square) -> Piece?): List<MoveMetadata> =
        squaresInRange(fromSquare)
            .asSequence()
            .map { square ->
                square to getPiece(square)?.let { PieceMetadata(it, square) }
            }
            .map { (square, occupant) ->
                val capture = if (occupant?.piece?.color == color.opposite) occupant else null
                val support = if (occupant?.piece?.color == color) occupant else null
                MoveMetadata(
                    move = Move.Basic(fromSquare, square),
                    piece = Piece(PieceKind.Knight, color),
                    capturing = capture,
                    supporting = support
                )
            }
            .toList()

    private fun squaresInRange(fromSquare: Square): List<Square> = listOfNotNull(
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
