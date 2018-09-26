package chesstastic.engine.entities.metadata.moves

import chesstastic.engine.entities.*
import chesstastic.engine.entities.metadata.HistoryMetadata.CastleMetadata

object KingMoves {
    fun calculate(color: Color, fromSquare: Square, pieces: Map<Square,Piece>, castleMetadata: CastleMetadata): Iterable<Move> {
        val regularMoves = adjacentSquares(fromSquare)
            .asSequence()
            .filterNot { pieces[it]?.color == color }
            .map { Move.Basic(fromSquare, it) }
            .toList()

        return regularMoves + castleMoves(color, pieces, castleMetadata)
    }

    private fun castleMoves(
        color: Color, pieces: Map<Square,Piece>, castleMetadata: CastleMetadata
    ): List<Move.Castle> {
        return if (!castleMetadata.kingHasMoved) {
            listOfNotNull(
                when {
                    castleMetadata.kingsideRookHasMoved -> null
                    castleMetadata.squares.kingsidePassing.any { pieces[it] != null } -> null
                    else -> Move.Castle.Kingside(color)
                },
                when {
                    castleMetadata.queensideRookHasMoved -> null
                    castleMetadata.squares.queensidePassing.any { pieces[it] != null } -> null
                    else -> Move.Castle.Queenside(color)
                }
            )
        } else listOf()
    }

    private fun adjacentSquares(fromSquare: Square): List<Square> =
        listOfNotNull(
            fromSquare.transform(1, 0),
            fromSquare.transform(1, 1),
            fromSquare.transform(0, 1),
            fromSquare.transform(-1, 0),
            fromSquare.transform(-1, -1),
            fromSquare.transform(0, -1),
            fromSquare.transform(1, -1),
            fromSquare.transform(-1, 1)
        )
}

