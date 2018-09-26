package chesstastic.engine.entities.metadata.moves

import chesstastic.engine.entities.*
import chesstastic.engine.entities.metadata.SquareMetadata

object KnightMoves {
    fun calculate(color: Color, fromSquare: Square, pieces: Map<Square, Piece>): Iterable<Move> =
        squaresInRange(fromSquare)
            .filterNot { pieces[it]?.color == color }
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
