package chesstastic.engine.calculators.moves

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
            .filterNot { (_, meta) -> meta?.piece?.color == color }
            .map { (square, maybeCaptured) ->
                MoveMetadata(Move.Basic(fromSquare, square), Piece(PieceKind.Knight, color), maybeCaptured)
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
