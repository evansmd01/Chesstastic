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
 *
 * This uses lots of mutable collections and does lots of scary, side-effecting by-ref mutations.
 * But it's all for the sake of minimizing cycles spent mapping, looping, and copying
 */
data class BoardMetadata(
    val board: Board,
    val squares: Map<Square, SquareMetadata>,
    val lightPlayer: PlayerMetadata,
    val darkPlayer: PlayerMetadata
) {
    companion object {
        fun from(board: Board): BoardMetadata {
            // Setup a bunch of mutable buckets to aggregate data efficiently
            val squareMetadata = mutableMapOf<Square, SquareMetadata>()

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
                allPieces = lightSquares,
                pawns = lightPawns,
                rooks = lightRooks,
                knights = lightKnights,
                bishops = lightBishops,
                queens = lightQueens,
                king = lightKingSquare ?: throw Exception("light king was missing from the board: $board"))
            val darkMetadata = PlayerMetadata(
                Dark,
                allPieces = darkSquares,
                pawns = darkPawns,
                rooks = darkRooks,
                knights = darkKnights,
                bishops = darkBishops,
                queens = darkQueens,
                king = darkKingSquare ?: throw Exception("dark king was missing from the board: $board"))

            val potentialBoard = PotentialBoard(
                board,
                squareMetadata,
                PotentialMoves(),
                PotentialMoves(),
                lightMetadata,
                darkMetadata
            )
            processMoves(potentialBoard)
            return potentialBoard.finalize()
        }

        private fun processMoves(board: PotentialBoard) {
            // pawn moves
            board.lightMoves.pawnMoves.addAll(board.lightMetadata.pawns.flatMap {
                PawnMoves.calculate(Light, it.square, board.getPiece, board.historyMetadata)
            })
            board.darkMoves.pawnMoves.addAll(board.darkMetadata.pawns.flatMap {
                PawnMoves.calculate(Dark, it.square, board.getPiece, board.historyMetadata)
            })
            // knight moves
            board.lightMoves.knightMoves.addAll(board.lightMetadata.knights.flatMap {
                KnightMoves.calculate(Light, it.square, board.getPiece)
            })
            board.darkMoves.knightMoves.addAll(board.darkMetadata.knights.flatMap {
                KnightMoves.calculate(Dark, it.square, board.getPiece)
            })
            // king moves
            board.lightMoves.kingMoves.addAll(
                KingMoves.calculate(Light, board.lightMetadata.king.square, board.getPiece, board.historyMetadata.lightCastleMetadata)
            )
            board.darkMoves.kingMoves.addAll(
                KingMoves.calculate(Dark, board.darkMetadata.king.square, board.getPiece, board.historyMetadata.darkCastleMetadata)
            )
            // queens and bishops
            processLineMoves(board.getPiece, board.squareMetadata, board.lightMoves,
                board.lightMetadata.rooksAndQueens, RookAndQueenMoves)
            processLineMoves(board.getPiece, board.squareMetadata, board.darkMoves,
                board.darkMetadata.rooksAndQueens, RookAndQueenMoves)
            // queens and rooks
            processLineMoves(board.getPiece, board.squareMetadata, board.lightMoves,
                board.lightMetadata.bishopsAndQueens, BishopAndQueenMoves)
            processLineMoves(board.getPiece, board.squareMetadata, board.darkMoves,
                board.darkMetadata.bishopsAndQueens, BishopAndQueenMoves)

            filterInvalidMoves(board)
        }

        private fun processLineMoves(
            getPiece: (Square) -> Piece?,
            squareMetadata: MutableMap<Square, SquareMetadata>,
            moves: PotentialMoves,
            fromPieces: Set<PieceMetadata>,
            calculator: LineMoves
        ) {
            fromPieces.forEach{ attackerMeta ->
                var attackedPiece: PieceMetadata? = null
                calculator.calculate(attackerMeta.square) { move ->
                    val foundPiece = getPiece(move.to)
                    val toSquareMeta = squareMetadata[move.to] ?: throw Exception("No metadata at ${move.to}")
                    val alreadyAttacked: PieceMetadata? = attackedPiece
                    when {
                        foundPiece?.color == attackerMeta.piece.color -> {
                            // ally is supported
                            squareMetadata[move.to] = toSquareMeta.copy(
                                isSupportedBy = toSquareMeta.isSupportedBy + attackerMeta
                            )
                            Stop // because movement is blocked by ally
                        }
                        alreadyAttacked != null && foundPiece?.color == attackerMeta.piece.color.opposite -> {
                            // enemy may be pinned or skewered
                            if (alreadyAttacked.piece.kind > foundPiece.kind) {
                                // skewer
                                val skewer = SkewerMetadata(
                                    skewered = alreadyAttacked,
                                    by = attackerMeta,
                                    to = PieceMetadata(foundPiece, move.to))
                                moves.skewers.add(skewer)
                                squareMetadata[alreadyAttacked.square] = toSquareMeta.copy(
                                    skewers = toSquareMeta.skewers + skewer
                                )
                            } else {
                                // pin
                                val pin = PinMetadata(
                                    pinned = alreadyAttacked,
                                    by = attackerMeta,
                                    to = PieceMetadata(foundPiece, move.to))
                                moves.pins.add(pin)
                                squareMetadata[alreadyAttacked.square] = toSquareMeta.copy(
                                    pins = toSquareMeta.pins + pin
                                )
                            }
                            Stop // no more attacks or moves
                        }
                        alreadyAttacked == null -> {
                            // empty square or foundPiece (if not null) is attacked
                            val maybeAttacked = foundPiece?.let { PieceMetadata(it, move.to) }
                            attackedPiece = maybeAttacked
                            squareMetadata[move.to] = toSquareMeta.copy(
                                isAttackedBy = toSquareMeta.isAttackedBy + attackerMeta
                            )
                            val moveMeta = MoveMetadata(move, attackerMeta.piece, maybeAttacked)
                            when (attackerMeta.piece.kind) {
                                Queen -> moves.queenMoves.add(moveMeta)
                                Bishop -> moves.bishopMoves.add(moveMeta)
                                Rook -> moves.rookMoves.add(moveMeta)
                                else -> throw Exception("Cannot process straight line moves for $attackerMeta")
                            }
                            KeepGoing // to look for move moves, pins, or skewers
                        }
                        else -> KeepGoing // still searching for pins or skewers
                    }
                }
            }
        }

        private fun filterInvalidMoves(board: PotentialBoard) {
            // remove pins that aren't worthwhile
            filterFalsePins(board.lightMoves, board)
            filterFalsePins(board.darkMoves, board)

            // remove skewers that aren't worthwhile
            filterFalseSkewers(board.lightMoves, board)
            filterFalseSkewers(board.darkMoves, board)

            // Filter moves from pieces which are pinned to the king before addressing potential checks
            // Because already pinned pieces won't be able to help defend their king
            board.lightMoves.pins.forEach { filterMovesIfPinnedToKing(it, board.lightMetadata, board.darkMoves, board) }
            board.darkMoves.pins.forEach { filterMovesIfPinnedToKing(it, board.darkMetadata, board.lightMoves, board) }

            // Then, if in check, filter moves that don't block the king or capture the attacker
            // (without moving any of those pinned pieces we just disabled)
            when (board.historyMetadata.currentTurn) {
                Light -> filterMovesIfKingInCheck(board.lightMoves, board.lightMetadata, board)
                Dark -> filterMovesIfKingInCheck(board.darkMoves, board.darkMetadata, board)
            }

            // Then filter moves that put the king himself into check
            filterInvalidKingMoves(CastleMetadata.CastleSquares.LIGHT, board.lightMoves, board)
            filterInvalidKingMoves(CastleMetadata.CastleSquares.DARK, board.darkMoves, board)
        }

        /**
         * This should be the mechanism called to remove any move for any reason
         */
        private fun disableMove(
            moveMeta: MoveMetadata,
            fromSet: MutableSet<MoveMetadata>,
            board: PotentialBoard
        ) {
            val pieceMeta = moveMeta.pieceMetadata

            // remove attacks from enemy pieces (except king, that would break check)
            // but don't remove attacks from empty squares, those are still valid for blockading the king
            if (moveMeta.capturing != null && moveMeta.capturing.piece.kind != King) {
                val attackedSquare = board.squareMetadata[moveMeta.move.to]
                    ?: throw Exception("Square ${moveMeta.move.to} not found")

                // it's not really skewered if the attack can't be made
                val skewer = attackedSquare.skewers.filter { it.by == pieceMeta }

                // unless pinned to king, any lesser pin is not really a pin if the attack can't be made
                val pin = attackedSquare.pins.filter { it.by == pieceMeta && it.to.piece.kind != King }

                board.squareMetadata[moveMeta.move.to] = attackedSquare.copy(
                    isAttackedBy = attackedSquare.isAttackedBy - pieceMeta,
                    skewers = attackedSquare.skewers - skewer,
                    pins = attackedSquare.pins - pin
                )
            }

            fromSet.remove(moveMeta)
        }

        private fun disableAll(moves: MutableSet<MoveMetadata>, board: PotentialBoard) {
            moves.toList().forEach { disableMove(it, moves, board) }
        }

        private fun disableUnless(moves: MutableSet<MoveMetadata>, board: PotentialBoard, shouldKeep: (MoveMetadata) -> Boolean) {
            moves.filterNot(shouldKeep).forEach { disableMove(it, moves, board) }
        }

        private fun filterFalsePins(moves: PotentialMoves, board: PotentialBoard) {
            val illegitimatePins = moves.pins.filterNot { pin -> isPieceAtRisk(pin.pinned, pin.by, board) }
            moves.pins.removeAll(illegitimatePins)
            illegitimatePins.forEach { pin ->
                val pinned = board.squareMetadata[pin.pinned.square]
                    ?: throw Exception("Could not find ${pin.pinned.square}")
                board.squareMetadata[pin.pinned.square] = pinned.copy(
                    pins = pinned.pins - pin
                )
            }
        }

        private fun filterFalseSkewers(moves: PotentialMoves, board: PotentialBoard) {
            val illegitimateSkewers = moves.skewers.filterNot { skewer ->
                // it's a legitimate skewer if both pieces are worth capturing, therefore the opponent has no good option
                isPieceAtRisk(skewer.skewered, skewer.by, board) && isPieceAtRisk(skewer.to, skewer.by, board)
            }
            moves.skewers.removeAll(illegitimateSkewers)
            illegitimateSkewers.forEach { skewer ->
                val skewered = board.squareMetadata[skewer.skewered.square]
                    ?: throw Exception("Could not find ${skewer.skewered.square}")
                board.squareMetadata[skewer.skewered.square] = skewered.copy(
                    skewers = skewered.skewers - skewer
                )
            }
        }

        /**
         * Determines if a piece is at risk of being captured, based on whether its a worthwhile trade
         * Does not analyze continuations of the exchange, just whether this one move is advantageous in the moment
         */
        private fun isPieceAtRisk(pieceMeta: PieceMetadata, attacker: PieceMetadata, board: PotentialBoard): Boolean {
            return when {
                // it's pinned to a higher value piece than the attacker, attacker would happily trade
                pieceMeta.piece.kind > attacker.piece.kind -> true // it's legit
                // it's pinned to a completely unsupported hanging piece, attacker would get a freebie
                board.squareMetadata[pieceMeta.square]?.isSupportedBy?.isEmpty() ?: true -> true // it's legit
                else -> false // it's not a legitimate pin
            }
        }

        /**
         * If a piece is pinned to it's king, it cannot move unless:
         *      - it is able to stay between the king and the attacker
         *      - it is able to capture the attacker
         *
         * Important: This should just remove MOVES, not attacks.
         * Just because a piece can't actually move, doesn't mean it's not still attacking squares,
         * applying pressure and potentially checking or blockading the enemy king
         */
        private fun filterMovesIfPinnedToKing(
            pin: PinMetadata,
            player: PlayerMetadata,
            moves: PotentialMoves,
            board: PotentialBoard
        ) {
            if (pin.to.piece.kind == King) {
                // for knights, disable all moves, no move can capture the pinning piece or stay blocking the king
                disableAll(moves.knightMoves, board)
                // for remaining pawns, rook, bishop, and queen moves, check if move stays between king and attacker
                val isValidMove = blocksCheckOrCaptures(pin.to, pin.by)
                disableUnless(moves.pawnMoves, board, isValidMove)
                disableUnless(moves.bishopMoves, board, isValidMove)
                disableUnless(moves.rookMoves, board, isValidMove)
                disableUnless(moves.queenMoves, board, isValidMove)

                // remove support from allies
                player.allPieces.forEach { ally ->
                    val allySquareMeta = board.squareMetadata[ally.square] ?: throw Exception("Could not find ${ally.square}")
                    board.squareMetadata[ally.square] = allySquareMeta.copy(
                        isSupportedBy = allySquareMeta.isSupportedBy - pin.pinned
                    )
                }
            }
        }

        /**
         * The king should not be allowed to move into check (moving to a square which is attacked)
         * They also can't castle if doing so passed through check (travels over a square which is attacked)
         */
        private fun filterInvalidKingMoves(
            castleSquares: CastleMetadata.CastleSquares,
            moves: PotentialMoves,
            board: PotentialBoard
        ) {
            moves.kingMoves.toList().forEach { moveMeta ->
                val shouldRemove = when (moveMeta.move) {
                    // remove castles that pass through attacked squares
                    is Move.Castle.Queenside -> castleSquares.queensidePassing
                    is Move.Castle.Kingside -> castleSquares.kingsidePassing
                    // remove moves from king into attacked squares
                    else -> listOf(moveMeta.move.to)
                }.any {
                    board.isSquareAttacked(it, attackerColor = moveMeta.piece.color.opposite)
                }
                if(shouldRemove) { disableMove(moveMeta, moves.kingMoves, board) }
            }
        }

        /**
         * Checks if the king is in check, and if so, removes any move that doesn't achieve one of the following:
         *   - Move the king to a square which is not attacked
         *   - Capture the attacker (if there is only one)
         *   - Block the attacker (if there is only one)
         */
        private fun filterMovesIfKingInCheck(
            moves: PotentialMoves,
            playerMetadata: PlayerMetadata,
            board: PotentialBoard
        ) {
            val king = playerMetadata.king
            val attackers = board.squareMetadata[king.square]?.attackedBy(king.piece.color.opposite) ?: emptyList()
            when (attackers.size) {
                0 -> return // filter nothing
                1 -> {
                    // filter out all but the king moves & moves that block or capture
                    val isValidMove = blocksCheckOrCaptures(king, attackers.first())
                    disableUnless(moves.knightMoves, board, isValidMove)
                    disableUnless(moves.pawnMoves, board, isValidMove)
                    disableUnless(moves.rookMoves, board, isValidMove)
                    disableUnless(moves.bishopMoves, board, isValidMove)
                    disableUnless(moves.queenMoves, board, isValidMove)
                }
                else -> {
                    // check from more than one attacker.
                    // no move can capture both or block both attackers
                    // so the only option is moving the king to safety
                    // so filter out all but king moves
                    disableAll(moves.pawnMoves, board)
                    disableAll(moves.rookMoves, board)
                    disableAll(moves.knightMoves, board)
                    disableAll(moves.bishopMoves, board)
                    disableAll(moves.queenMoves, board)
                }
            }
        }

        /**
         * Creates a function that checks if a move will block the king from check or capture the attacker
         */
        private fun blocksCheckOrCaptures(
            kingInCheck: PieceMetadata,
            attacker: PieceMetadata
        ): (MoveMetadata) -> Boolean {
            val pathToAttacker = kingInCheck.square.pathTo(attacker.square)
            return { it.move.to in pathToAttacker }
        }
    }
}

/**
 * Helper model for holding a bunch of mutable state while aggregating everything
 * during the big board metadata algorithm.
 *
 * Outside of this algorithm, I want the board metadata to be immutable,
 * so these are separate models for holding mutable collections
 */
private data class PotentialBoard(
    private val board: Board,
    val squareMetadata: MutableMap<Square, SquareMetadata>,
    val lightMoves: PotentialMoves,
    val darkMoves: PotentialMoves,
    val lightMetadata: PlayerMetadata,
    val darkMetadata: PlayerMetadata
) {
    val historyMetadata = board.historyMetadata

    val getPiece: (square: Square) -> Piece? = { board[it] }

    fun isSquareAttacked(
        square: Square,
        attackerColor: Color
    ): Boolean = squareMetadata[square]?.isAttackedBy?.any {
        it.piece.color == attackerColor
    } ?: false

    fun finalize() = BoardMetadata(
        board,
        squares = squareMetadata,
        lightPlayer = lightMetadata.copy(moves = lightMoves.finalize()),
        darkPlayer = darkMetadata.copy(moves = darkMoves.finalize())
    )
}

private data class PotentialMoves(
    val pawnMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val bishopMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val knightMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val rookMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val queenMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val kingMoves: MutableSet<MoveMetadata> = mutableSetOf(),
    val pins: MutableSet<PinMetadata> = mutableSetOf(),
    val skewers: MutableSet<SkewerMetadata> = mutableSetOf()
) {
    fun finalize() = PlayerMetadata.Moves(
        pawnMoves = pawnMoves,
        bishopMoves = bishopMoves,
        knightMoves = knightMoves,
        rookMoves = rookMoves,
        queenMoves = queenMoves,
        kingMoves = kingMoves,
        pins = pins,
        skewers = skewers
    )
}
