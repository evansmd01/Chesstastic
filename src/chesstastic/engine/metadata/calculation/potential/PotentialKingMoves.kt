package chesstastic.engine.metadata.calculation.potential

import chesstastic.engine.entities.*
import chesstastic.engine.metadata.CastleMetadata
import chesstastic.engine.metadata.MoveMetadata
import chesstastic.engine.metadata.PieceMetadata

object PotentialKingMoves {
    fun calculate(color: Color, fromSquare: Square, getPiece: (Square) -> Piece?, castleMetadata: CastleMetadata): List<MoveMetadata> {
        val regularMoves = adjacentSquares(fromSquare)
            .asSequence()
            .map { square ->
                square to getPiece(square)?.let { PieceMetadata(it, square) }
            }
            .map { (square, occupant) ->
                val capture = if (occupant?.piece?.color == color.opposite) occupant else null
                val support = if (occupant?.piece?.color == color) occupant else null
                MoveMetadata(
                    move = Move.Basic(fromSquare, square),
                    piece = Piece(PieceKind.King, color),
                    capturing = capture,
                    supporting = support
                )
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
                    castleMetadata.kingsideRookMovedOrCaptured -> null
                    castleMetadata.squares.kingsideBlocking.any { getPiece(it) != null } -> null
                    else -> MoveMetadata(Move.Castle.Kingside(color), Piece(PieceKind.King, color), null, null)
                },
                when {
                    castleMetadata.queensideRookMovedOrCaptured -> null
                    castleMetadata.squares.queensideBlocking.any { getPiece(it) != null } -> null
                    else -> MoveMetadata(Move.Castle.Queenside(color), Piece(PieceKind.King, color), null, null)
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

