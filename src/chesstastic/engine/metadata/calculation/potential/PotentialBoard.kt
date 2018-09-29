package chesstastic.engine.metadata.calculation.potential

import chesstastic.engine.entities.*
import chesstastic.engine.metadata.*

/**
 * Helper model for holding a bunch of mutable state while aggregating everything
 * during the big board metadata algorithm.
 *
 * Outside of this algorithm, I want the board metadata to be immutable,
 * so these are separate models for holding mutable collections
 */
data class PotentialBoard(
    private val board: Board,
    val squares: Map<Square, PotentialSquare>,
    val lightMoves: PotentialMoves,
    val darkMoves: PotentialMoves,
    val lightMetadata: PlayerMetadata,
    val darkMetadata: PlayerMetadata
) {
    val historyMetadata = board.historyMetadata

    val getPiece: (square: Square) -> Piece? = { board[it] }

    fun isNotSafeToMove(color: Color, toSquare: Square): Boolean {
        val squareMeta = squares[toSquare]
            ?: throw Exception("Can find square meta at $toSquare")
        return squareMeta.isAttackedBy.any { it.piece.color == color.opposite }
            || squareMeta.isSupportedBy.any { it.piece.color == color.opposite }
    }

    fun finalize() = BoardMetadata(
        board,
        squares = squares.map{ (square, data) -> square to data.finalize() }.toMap(),
        lightPlayer = lightMetadata.copy(moves = lightMoves.finalize()),
        darkPlayer = darkMetadata.copy(moves = darkMoves.finalize())
    )

    companion object {
        fun from(board: Board): PotentialBoard {
            val squareMetadata = mutableMapOf<Square, PotentialSquare>()

            val lightSquares = mutableSetOf<PieceMetadata>()
            val lightPawns = mutableSetOf<PieceMetadata>()
            val lightRooks = mutableSetOf<PieceMetadata>()
            val lightKnights = mutableSetOf<PieceMetadata>()
            val lightBishops = mutableSetOf<PieceMetadata>()
            val lightQueens = mutableSetOf<PieceMetadata>()
            var lightKingSquare: PieceMetadata? = null

            val darkSquares = mutableSetOf<PieceMetadata>()
            val darkPawns = mutableSetOf<PieceMetadata>()
            val darkRooks = mutableSetOf<PieceMetadata>()
            val darkKnights = mutableSetOf<PieceMetadata>()
            val darkBishops = mutableSetOf<PieceMetadata>()
            val darkQueens = mutableSetOf<PieceMetadata>()
            var darkKingSquare: PieceMetadata? = null

            // Keeping to a SINGLE loop, gather all the squares/pieces
            Board.SQUARES.forEach { square ->
                val piece = board[square]
                if (piece != null) {
                    if (piece.color == Color.Light) {
                        lightSquares.add(PieceMetadata(piece, square))
                        when (piece.kind) {
                            PieceKind.Pawn -> lightPawns.add(PieceMetadata(piece, square))
                            PieceKind.Rook -> lightRooks.add(PieceMetadata(piece, square))
                            PieceKind.Knight -> lightKnights.add(PieceMetadata(piece, square))
                            PieceKind.Bishop -> lightBishops.add(PieceMetadata(piece, square))
                            PieceKind.Queen -> lightQueens.add(PieceMetadata(piece, square))
                            PieceKind.King -> lightKingSquare = PieceMetadata(piece, square)
                        }
                    } else {
                        darkSquares.add(PieceMetadata(piece, square))
                        when (piece.kind) {
                            PieceKind.Pawn -> darkPawns.add(PieceMetadata(piece, square))
                            PieceKind.Rook -> darkRooks.add(PieceMetadata(piece, square))
                            PieceKind.Knight -> darkKnights.add(PieceMetadata(piece, square))
                            PieceKind.Bishop -> darkBishops.add(PieceMetadata(piece, square))
                            PieceKind.Queen -> darkQueens.add(PieceMetadata(piece, square))
                            PieceKind.King -> darkKingSquare = PieceMetadata(piece, square)
                        }
                    }
                }
                squareMetadata[square] = PotentialSquare.from(square, piece)
            }

            // materialize all that mutable
            val lightMetadata = PlayerMetadata(
                Color.Light,
                allPieces = lightSquares,
                pawns = lightPawns,
                rooks = lightRooks,
                knights = lightKnights,
                bishops = lightBishops,
                queens = lightQueens,
                king = lightKingSquare ?: throw Exception("light king was missing from the board: $board"))
            val darkMetadata = PlayerMetadata(
                Color.Dark,
                allPieces = darkSquares,
                pawns = darkPawns,
                rooks = darkRooks,
                knights = darkKnights,
                bishops = darkBishops,
                queens = darkQueens,
                king = darkKingSquare ?: throw Exception("dark king was missing from the board: $board"))

            return PotentialBoard(
                board,
                squareMetadata,
                PotentialMoves(),
                PotentialMoves(),
                lightMetadata,
                darkMetadata
            )
        }
    }
}

