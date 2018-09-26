package chesstastic.engine.entities.metadata

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.entities.metadata.moves.*
import chesstastic.engine.entities.metadata.moves.LineMoves.Continuation.*

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
                    pieces[square] = piece
                    if (piece.color == Light) {
                        lightSquares.add(PieceMetadata(piece, square))
                        when (piece.kind) {
                            Pawn -> lightPawns.add(PieceMetadata(piece, square))
                            Rook -> lightRooks.add(PieceMetadata(piece, square))
                            Knight -> lightKnights.add(PieceMetadata(piece, square))
                            Bishop -> lightBishops.add(PieceMetadata(piece, square))
                            Queen -> lightQueens.add(PieceMetadata(piece, square))
                            King -> lightKingSquare = PieceMetadata(piece, square)
                        }
                    } else {
                        darkSquares.add(PieceMetadata(piece, square))
                        when (piece.kind) {
                            Pawn -> darkPawns.add(PieceMetadata(piece, square))
                            Rook -> darkRooks.add(PieceMetadata(piece, square))
                            Knight -> darkKnights.add(PieceMetadata(piece, square))
                            Bishop -> darkBishops.add(PieceMetadata(piece, square))
                            Queen -> darkQueens.add(PieceMetadata(piece, square))
                            King -> darkKingSquare = PieceMetadata(piece, square)
                        }
                    }
                }
                squareMetadata[square] = SquareMetadata.from(square, piece)
            }

            // materialize all that mutable
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
                PawnMoves.calculate(Light, it.square, pieces, historyMetadata)
            })
            darkMoves.addAll(darkMetadata.pawns.flatMap {
                PawnMoves.calculate(Dark, it.square, pieces, historyMetadata)
            })
            // knight moves
            lightMoves.addAll(lightMetadata.knights.flatMap {
                KnightMoves.calculate(Light, it.square, pieces)
            })
            darkMoves.addAll(darkMetadata.knights.flatMap {
                KnightMoves.calculate(Dark, it.square, pieces)
            })
            // king moves
            lightMoves.addAll(
                KingMoves.calculate(Light, lightMetadata.king.square, pieces, historyMetadata.lightCastleMetadata)
            )
            darkMoves.addAll(
                KingMoves.calculate(Dark, darkMetadata.king.square, pieces, historyMetadata.darkCastleMetadata)
            )
            // queens and bishops
            processLine(pieces, squareMetadata, lightMoves, lightMetadata.horizontalPieces, HorizontalMoves)
            processLine(pieces, squareMetadata, lightMoves, lightMetadata.diagonalPieces, DiagonalMoves)
            // queens and rooks
            processLine(pieces, squareMetadata, darkMoves, darkMetadata.horizontalPieces, HorizontalMoves)
            processLine(pieces, squareMetadata, darkMoves, darkMetadata.diagonalPieces, DiagonalMoves)

            return validateMoves(squareMetadata,
                lightMetadata.copy(moves = lightMoves),
                darkMetadata.copy(moves = darkMoves))
        }

        private fun processLine(
            pieces: Map<Square, Piece>,
            squareMetadata: MutableMap<Square, SquareMetadata>,
            moves: MutableSet<Move>,
            fromPieces: Set<PieceMetadata>,
            calculator: LineMoves
        ) {
            fromPieces.forEach{ attackerMeta ->
                var attackedPiece: PieceMetadata? = null
                calculator.calculate(attackerMeta.square) { move ->
                    val foundPiece = pieces[move.to]
                    val meta = squareMetadata[move.to] ?: throw Error("No metadata at ${move.to}")
                    val alreadyAttacked: PieceMetadata? = attackedPiece
                    when {
                        foundPiece?.color == attackerMeta.piece.color -> {
                            // ally is supported
                            squareMetadata[move.to] = meta.copy(
                                supportedBy = meta.supportedBy + attackerMeta
                            )
                            Stop // because movement is blocked by ally
                        }
                        alreadyAttacked != null && foundPiece?.color == attackerMeta.piece.color.opposite -> {
                            // enemy may be pinned or skewered
                            if (alreadyAttacked.piece.kind > foundPiece.kind) {
                                // skewer
                                squareMetadata[alreadyAttacked.square] = meta.copy(
                                    skewers = meta.skewers + SkewerMetadata(
                                        by = attackerMeta,
                                        to = PieceMetadata(foundPiece, move.to))
                                )
                            } else {
                                // pin
                                squareMetadata[alreadyAttacked.square] = meta.copy(
                                    pins = meta.pins + PinMetadata(
                                        by = attackerMeta,
                                        to = PieceMetadata(foundPiece, move.to))
                                )
                            }
                            Stop // no more attacks or moves
                        }
                        alreadyAttacked == null -> {
                            // empty square or foundPiece (if not null) is attacked
                            attackedPiece = foundPiece?.let { PieceMetadata(it, move.to) }
                            squareMetadata[move.to] = meta.copy(
                                attackedBy = meta.attackedBy + attackerMeta
                            )
                            moves.add(move)
                            KeepGoing // to look for move moves, pins, or skewers
                        }
                        else -> KeepGoing // still searching for pins or skewers
                    }
                }
            }
        }

        private fun validateMoves(
            squares: MutableMap<Square, SquareMetadata>,
            lightMetadata: PlayerMetadata,
            darkMetadata: PlayerMetadata
        ): Map<Square, SquareMetadata> {
            // remove moves from pieces pinned to the king

            // remove moves from king into attacked squares

            // remove castles that pass through attacked squares

            // remove pins that aren't really pins

            // remove skewers that aren't really skewers

            return squares
        }
    }
}

