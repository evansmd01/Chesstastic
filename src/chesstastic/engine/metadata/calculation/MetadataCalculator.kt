package chesstastic.engine.metadata.calculation

import chesstastic.engine.metadata.calculation.models.PotentialBoard
import chesstastic.engine.metadata.calculation.models.PotentialMoves
import chesstastic.engine.metadata.calculation.moves.*
import chesstastic.engine.entities.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.metadata.*

object MetadataCalculator {
    fun calculate(board: Board): BoardMetadata {
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
            PawnMoveCalculator.calculate(Light, it.square, board.getPiece, board.historyMetadata)
                .mapNotNull { move -> processMove(move, board.squareMetadata) }
        })
        board.darkMoves.pawnMoves.addAll(board.darkMetadata.pawns.flatMap {
            PawnMoveCalculator.calculate(Dark, it.square, board.getPiece, board.historyMetadata)
                .mapNotNull { move -> processMove(move, board.squareMetadata) }
        })
        // knight moves
        board.lightMoves.knightMoves.addAll(board.lightMetadata.knights.flatMap {
            KnightMoveCalculator.calculate(Light, it.square, board.getPiece)
                .mapNotNull { move -> processMove(move, board.squareMetadata) }
        })
        board.darkMoves.knightMoves.addAll(board.darkMetadata.knights.flatMap {
            KnightMoveCalculator.calculate(Dark, it.square, board.getPiece)
                .mapNotNull { move -> processMove(move, board.squareMetadata) }
        })
        // king moves
        board.lightMoves.kingMoves.addAll(
            KingMoveCalculator.calculate(Light, board.lightMetadata.king.square, board.getPiece, board.historyMetadata.lightCastleMetadata)
                .mapNotNull { move -> processMove(move, board.squareMetadata) }
        )
        board.darkMoves.kingMoves.addAll(
            KingMoveCalculator.calculate(Dark, board.darkMetadata.king.square, board.getPiece, board.historyMetadata.darkCastleMetadata)
                .mapNotNull { move -> processMove(move, board.squareMetadata) }
        )

        // queens and bishops
        processLineMoves(board, board.lightMoves,
            board.lightMetadata.rooksAndQueens, RookAndQueenMoveCalculator)
        processLineMoves(board, board.darkMoves,
            board.darkMetadata.rooksAndQueens, RookAndQueenMoveCalculator)
        // queens and rooks
        processLineMoves(board, board.lightMoves,
            board.lightMetadata.bishopsAndQueens, BishopAndQueenMoveCalculator)
        processLineMoves(board, board.darkMoves,
            board.darkMetadata.bishopsAndQueens, BishopAndQueenMoveCalculator)

        filterInvalidMoves(board)
    }

    private fun processMove(
        moveMeta: MoveMetadata,
        squareMetadata: MutableMap<Square, SquareMetadata>
    ): MoveMetadata? {
        val toSquareMeta = squareMetadata[moveMeta.move.to] ?: throw Exception("No metadata at ${moveMeta.move.to}")
        when {
            // if supporting, mark support and filter out move, because you can't move on top of your pieces
            moveMeta.supporting != null -> {
                squareMetadata[moveMeta.move.to] = toSquareMeta.copy(
                    isSupportedBy = toSquareMeta.isSupportedBy + moveMeta.pieceMetadata
                )
                return null
            }
            // otherwise, mark the empty square, or occupied enemy square as attacked.
            else -> {
                squareMetadata[moveMeta.move.to] = toSquareMeta.copy(
                    isAttackedBy = toSquareMeta.isAttackedBy + moveMeta.pieceMetadata
                )
            }
        }
        return moveMeta
    }

    private fun processLineMoves(
        board: PotentialBoard,
        moves: PotentialMoves,
        fromPieces: Set<PieceMetadata>,
        calculator: LineMoveCalculator
    ) {
        fromPieces.forEach{ attacker ->
            calculator.calculate(attacker.square, attacker.piece, board.getPiece) { moveMeta, previousCapture ->
                when {
                    moveMeta.supporting != null -> {
                        // ally is supported
                        val toSquareMeta = board.squareMetadata[moveMeta.move.to]
                            ?: throw Exception("No metadata at ${moveMeta.move.to}")
                        board.squareMetadata[moveMeta.move.to] = toSquareMeta.copy(
                            isSupportedBy = toSquareMeta.isSupportedBy + attacker
                        )
                        LineMoveCalculator.Continuation.Stop // because movement is blocked by ally
                    }
                    previousCapture != null && moveMeta.capturing != null -> {
                        // enemy may be pinned or skewered
                        if (previousCapture.piece.kind > moveMeta.capturing.piece.kind) {
                            // skewer
                            val skewer = SkewerMetadata(
                                skewered = previousCapture,
                                by = attacker,
                                to = moveMeta.capturing
                            )
                            moves.skewers.add(skewer)
                            val prevSquareMeta = board.squareMetadata[previousCapture.square]
                                ?: throw Exception("No metadata at ${previousCapture.square}")
                            board.squareMetadata[previousCapture.square] = prevSquareMeta.copy(
                                skewers = prevSquareMeta.skewers + skewer
                            )
                        } else {
                            // pin
                            val pin = PinMetadata(
                                pinned = previousCapture,
                                by = attacker,
                                to = moveMeta.capturing
                            )
                            moves.pins.add(pin)
                            val prevSquareMeta = board.squareMetadata[previousCapture.square]
                                ?: throw Exception("No metadata at ${previousCapture.square}")
                            board.squareMetadata[previousCapture.square] = prevSquareMeta.copy(
                                pins = prevSquareMeta.pins + pin
                            )
                        }
                        LineMoveCalculator.Continuation.Stop // no more attacks or moves
                    }
                    previousCapture == null -> {
                        val toSquareMeta = board.squareMetadata[moveMeta.move.to]
                            ?: throw Exception("No metadata at ${moveMeta.move.to}")
                        board.squareMetadata[moveMeta.move.to] = toSquareMeta.copy(
                            isAttackedBy = toSquareMeta.isAttackedBy + attacker
                        )
                        when (attacker.piece.kind) {
                            Queen -> moves.queenMoves.add(moveMeta)
                            Bishop -> moves.bishopMoves.add(moveMeta)
                            Rook -> moves.rookMoves.add(moveMeta)
                            else -> throw Exception("Cannot process straight line moves for $attacker")
                        }
                        LineMoveCalculator.Continuation.KeepGoing // to look for move moves, pins, or skewers
                    }
                    else -> LineMoveCalculator.Continuation.KeepGoing // still searching for pins or skewers
                }
            }
        }
    }

    private fun filterInvalidMoves(board: PotentialBoard) {
        // remove invalid diagonal pawn moves
        filterNonCapturingPawnMoves(board.lightMoves, board)
        filterNonCapturingPawnMoves(board.darkMoves, board)

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

    private fun filterNonCapturingPawnMoves(moves: PotentialMoves, board: PotentialBoard) {
        moves.pawnMoves.asSequence().filterNot { moveMeta ->
            when {
                // allow capturing
                moveMeta.capturing != null -> true
                // allow forward
                moveMeta.move.to.file == moveMeta.move.from.file -> {
                    // but the square in from of the pawn is not attacked, since they can't capture forward
                    val meta = board.squareMetadata[moveMeta.move.to]!!
                    board.squareMetadata[moveMeta.move.to] = meta.copy(
                        isAttackedBy = meta.isAttackedBy - moveMeta.pieceMetadata
                    )
                    true
                }
                // else it's a diagonal non-capturing move
                else -> false // illegitimate
            }
        }.toList().forEach { disableMove(it, moves.pawnMoves, board) }
    }

    private fun filterFalsePins(moves: PotentialMoves, board: PotentialBoard) {
        val illegitimatePins = moves.pins.filterNot { pin ->
            pin.to.piece.kind == King || isPieceAtRisk(pin.pinned, pin.by, board)
        }
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
            val isValidMove = blocksCheckOrCaptures(pin.to, pin.by)
            val containingSet = moves.movesFor(pin.pinned.piece.kind)
            val pinnedPieceMoves = containingSet.filter { it.move.from == pin.pinned.square }
            pinnedPieceMoves.forEach {
                if (!isValidMove(it))
                    disableMove(it, containingSet, board)
            }

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
                is Move.Castle.Queenside -> castleSquares.queensidePassingCheck + moveMeta.move.from
                is Move.Castle.Kingside -> castleSquares.kingsidePassingCheck + moveMeta.move.from
                // remove moves from king into attacked squares
                else -> listOf(moveMeta.move.to)
            }.any {
                board.isNotSafeToMove(moveMeta.piece.color, it)
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
                // and check that king moves don't stay in check
                filterKingMovesThatRemainInCheck(king, attackers, moves.kingMoves, board)
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
                // and check that king moves don't stay in check
                filterKingMovesThatRemainInCheck(king, attackers, moves.kingMoves, board)
            }
        }
    }

    /**
     * there are already other checks to ensure the king doesn't move INTO check
     * but we need to add an extra check here that the king doesn't move
     * to the square opposite it's attacker (if attacker is a straight line)
     * that square behind the king won't be marked as attacked, so it will look like a valid move
     * but the king will still be attacked there.
     */
    private fun filterKingMovesThatRemainInCheck(
        king: PieceMetadata,
        attackers: List<PieceMetadata>,
        kingMoves: MutableSet<MoveMetadata>,
        board: PotentialBoard
    ) {
        attackers.filter { it.piece.kind in setOf(Queen, Rook, Bishop) }
            .forEach { attackerMeta ->
                val moveBack = king.square.moveAwayFrom(attackerMeta.square)
                kingMoves.filter { it.move.to == moveBack }
                    .forEach{ disableMove(it, kingMoves, board) }
            }
    }

    /**
     * Creates a function that checks if a move will block the king from check or capture the attacker
     */
    private fun blocksCheckOrCaptures(
        kingInCheck: PieceMetadata,
        attacker: PieceMetadata
    ): (MoveMetadata) -> Boolean {
        if (attacker.piece.kind == Knight) // can't block knight, only capture
            return { it.move.to == attacker.square }
        // otherwise, any move between the king and the attacker, or capturing the attacker, is valid.
        val pathToAttacker = kingInCheck.square.findPathTo(attacker.square)
        return { it.move.to in pathToAttacker }
    }
}
