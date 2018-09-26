package chesstastic.engine.entities.metadata

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.entities.metadata.moves.KingMoves
import chesstastic.engine.entities.metadata.moves.KnightMoves
import chesstastic.engine.entities.metadata.moves.PawnMoves

/**
 * Precomputed information about the state of the position
 * which can be used for many different purposes,
 * such as validating moves, declaring check, checkmate, or stalemate,
 * or for analyzing various heuristics to determine the favorability of the position
 *
 * This serves as an optimization to prevent wasting cycles
 * looping over the board to collect information separately for each purpose.
 */
data class BoardMetadata(
    val board: Board,
    val squareMetadata: Map<Square, SquareMetadata>,
    val lightMetadata: PlayerMetadata,
    val darkMetadata: PlayerMetadata
) {
    companion object {
        fun from(board: Board): BoardMetadata {
            // Setup a bunch of mutable buckets to aggregate data efficiently
            val squareMetadata = mutableMapOf<Square, SquareMetadata>()
            val pieces = mutableMapOf<Square, Piece>()

            val lightSquares = mutableSetOf<Square>()
            val lightPawns = mutableSetOf<Square>()
            val lightRooks = mutableSetOf<Square>()
            val lightKnights = mutableSetOf<Square>()
            val lightBishops = mutableSetOf<Square>()
            val lightQueens = mutableSetOf<Square>()
            var lightKingSquare: Square? = null

            val darkSquares = mutableSetOf<Square>()
            val darkPawns = mutableSetOf<Square>()
            val darkRooks = mutableSetOf<Square>()
            val darkKnights = mutableSetOf<Square>()
            val darkBishops = mutableSetOf<Square>()
            val darkQueens = mutableSetOf<Square>()
            var darkKingSquare: Square? = null

            // Keeping to a SINGLE loop, gather all the squares/pieces
            Board.SQUARES.forEach { square ->
                val piece = board[square]
                if (piece != null) {
                    pieces[square] = piece
                    if (piece.color == Light) {
                        lightSquares.add(square)
                        when (piece.kind) {
                            Pawn -> lightPawns.add(square)
                            Rook -> lightRooks.add(square)
                            Knight -> lightKnights.add(square)
                            Bishop -> lightBishops.add(square)
                            Queen -> lightQueens.add(square)
                            King -> lightKingSquare = square
                        }
                    } else {
                        darkSquares.add(square)
                        when (piece.kind) {
                            Pawn -> darkPawns.add(square)
                            Rook -> darkRooks.add(square)
                            Knight -> darkKnights.add(square)
                            Bishop -> darkBishops.add(square)
                            Queen -> darkQueens.add(square)
                            King -> darkKingSquare = square
                        }
                    }
                }
                squareMetadata[square] = SquareMetadata(square, piece)
            }

            // materialize all that mutable data into these immutable types
            val lightMetadata = PlayerMetadata(
                Light,
                pieces = lightSquares,
                pawns = lightPawns,
                rooks = lightRooks,
                knights = lightKnights,
                bishops = lightBishops,
                queens = lightQueens,
                king = lightKingSquare ?: throw Exception("light king was missing from the board: $board"))
            val darkMetadata = PlayerMetadata(
                Dark,
                pieces = darkSquares,
                pawns = darkPawns,
                rooks = darkRooks,
                knights = darkKnights,
                bishops = darkBishops,
                queens = darkQueens,
                king = darkKingSquare ?: throw Exception("dark king was missing from the board: $board"))

            return BoardMetadata(
                board,
                squareMetadata = processMoves(
                    pieces, squareMetadata, board.historyMetadata, lightMetadata, darkMetadata
                ),
                lightMetadata = lightMetadata,
                darkMetadata = darkMetadata
            )
        }

        private fun processMoves(
            pieces: Map<Square, Piece>,
            squareMetadata: MutableMap<Square, SquareMetadata>,
            historyMetadata: HistoryMetadata,
            lightMetadata: PlayerMetadata,
            darkMetadata: PlayerMetadata
        ): Map<Square, SquareMetadata> {
            val lightMoves = mutableSetOf<Move>()
            val darkMoves = mutableSetOf<Move>()
            // pawn moves
            lightMoves.addAll(lightMetadata.pawns.flatMap {
                PawnMoves.calculate(Light, it, pieces, historyMetadata)
            })
            darkMoves.addAll(darkMetadata.pawns.flatMap {
                PawnMoves.calculate(Dark, it, pieces, historyMetadata)
            })
            // knight moves
            lightMoves.addAll(lightMetadata.knights.flatMap {
                KnightMoves.calculate(Light, it, pieces)
            })
            darkMoves.addAll(darkMetadata.knights.flatMap {
                KnightMoves.calculate(Dark, it, pieces)
            })
            // king moves
            lightMoves.addAll(
                KingMoves.calculate(Light, lightMetadata.king, pieces, historyMetadata.lightCastleMetadata)
            )
            darkMoves.addAll(
                KingMoves.calculate(Dark, darkMetadata.king, pieces, historyMetadata.darkCastleMetadata)
            )
            // straight line pieces
                // moves

                // pins

                // skewers

            return validateMoves(squareMetadata,
                lightMetadata.copy(moves = lightMoves),
                darkMetadata.copy(moves = darkMoves))
        }

        private fun validateMoves(
            squares: MutableMap<Square, SquareMetadata>,
            lightMetadata: PlayerMetadata,
            darkMetadata: PlayerMetadata
        ): Map<Square, SquareMetadata> {
            // remove moves from pieces pinned to the king

            // remove moves from king into attacked squares

            // remove castles that pass through attacked squares

            return squares
        }
    }
}

/**
 * contextual information about a square to be reused in various computations without wasting cycles recomputing
 */
data class SquareMetadata(
    val square: Square,
    val occupant: Piece?
)

/**
 * Contextual information about one of the players' positions
 */
data class PlayerMetadata(
    val color: Color,
    val pieces: Set<Square>,
    val pawns: Set<Square>,
    val rooks: Set<Square>,
    val knights: Set<Square>,
    val bishops: Set<Square>,
    val queens: Set<Square>,
    val king: Square,
    val moves: Set<Move> = emptySet()
)

