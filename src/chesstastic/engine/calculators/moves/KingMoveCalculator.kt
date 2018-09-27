package chesstastic.engine.calculators.moves

import chesstastic.engine.entities.*
import chesstastic.engine.metadata.CastleMetadata
import chesstastic.engine.metadata.MoveMetadata
import chesstastic.engine.metadata.PieceMetadata

object KingMoveCalculator {
    fun calculate(color: Color, fromSquare: Square, getPiece: (Square) -> Piece?, castleMetadata: CastleMetadata): List<MoveMetadata> {
        val regularMoves = adjacentSquares(fromSquare)
            .asSequence()
            .map { square ->
                square to getPiece(square)?.let { PieceMetadata(it, square) }
            }
            .filterNot { (_, meta) -> meta?.piece?.color == color }
            .map { (square, maybeCaptured) ->
                MoveMetadata(Move.Basic(fromSquare, square), Piece(PieceKind.King, color), maybeCaptured)
            }
            .toList()

        return regularMoves + castleMoves(color, getPiece, castleMetadata)
    }

    private fun castleMoves(
        color: Color, getPiece: (Square) -> Piece?, castleMetadata: CastleMetadata
    ): List<MoveMetadata> {
        return if (!castleMetadata.kingHasMoved) {
            listOfNotNull(
                when {
                    castleMetadata.kingsideRookHasMoved -> null
                    castleMetadata.squares.kingsidePassing.any { getPiece(it) != null } -> null
                    else -> MoveMetadata(Move.Castle.Kingside(color), Piece(PieceKind.King, color), null)
                },
                when {
                    castleMetadata.queensideRookHasMoved -> null
                    castleMetadata.squares.queensidePassing.any { getPiece(it) != null } -> null
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

