package chesstastic.engine.entities.metadata.moves

import chesstastic.engine.entities.*
import chesstastic.engine.entities.metadata.CastleMetadata
import chesstastic.engine.entities.metadata.MoveMetadata
import chesstastic.engine.entities.metadata.PieceMetadata

object KingMoves {
    fun calculate(color: Color, fromSquare: Square, pieces: Map<Square,Piece>, castleMetadata: CastleMetadata): List<MoveMetadata> {
        val regularMoves = adjacentSquares(fromSquare)
            .asSequence()
            .map { square ->
                square to pieces[square]?.let { PieceMetadata(it, square) }
            }
            .filterNot { (_, meta) -> meta?.piece?.color == color }
            .map { (square, maybeCaptured) ->
                MoveMetadata(Move.Basic(fromSquare, square), Piece(PieceKind.King, color), maybeCaptured)
            }
            .toList()

        return regularMoves + castleMoves(color, pieces, castleMetadata)
    }

    private fun castleMoves(
        color: Color, pieces: Map<Square,Piece>, castleMetadata: CastleMetadata
    ): List<MoveMetadata> {
        return if (!castleMetadata.kingHasMoved) {
            listOfNotNull(
                when {
                    castleMetadata.kingsideRookHasMoved -> null
                    castleMetadata.squares.kingsidePassing.any { pieces[it] != null } -> null
                    else -> MoveMetadata(Move.Castle.Kingside(color), Piece(PieceKind.King, color), null)
                },
                when {
                    castleMetadata.queensideRookHasMoved -> null
                    castleMetadata.squares.queensidePassing.any { pieces[it] != null } -> null
                    else -> MoveMetadata(Move.Castle.Queenside(color), Piece(PieceKind.King, color), null)
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

