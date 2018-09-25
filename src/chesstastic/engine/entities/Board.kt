package chesstastic.engine.entities

import chesstastic.engine.position.calculators.BoardCalculator
import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.entities.Color.*


class Board(
    private val state: Array<Array<Piece?>> = INITIAL_STATE,
    val historyMetadata: HistoryMetadata = HistoryMetadata.EMPTY
) {
    operator fun get(coord: Square): Piece? = state[coord.rank.index][coord.file.index]

    val legalMoves by lazy { BoardCalculator.legalMoves(this, historyMetadata.currentTurn) }

    val isCheck by lazy { isInCheck(historyMetadata.currentTurn) }
    fun isInCheck(color: Color) = BoardCalculator.isKingInCheck(color, this)
    val isCheckmate by lazy { legalMoves.count() == 0 && isCheck }
    private val inactivityLimit = 100 // TODO: Verify the "50 move rule" defines a "move" as both players having taken a turn, hence 100 here.
    private val remainingPieces by lazy { state.sumBy { it.filterNotNull().count() } }
    val isStalemate by lazy { historyMetadata.inactivityCount >= inactivityLimit || (legalMoves.count() == 0 && !isCheck) || remainingPieces <= 2 }
    val isGameOver by lazy { isStalemate || isCheckmate }

    fun isSquareAttacked(square: Square, attacker: Color) = BoardCalculator.isSquareAttacked(square, attacker, this)

    fun kingSquare(color: Color): Square  {
        return SQUARES.firstOrNull { square ->
            val piece = this[square]
            when {
                piece?.kind == King && piece.color == color -> true
                else -> false
            }
        } ?: throw Error("King is missing from the board.\nHistory: ${historyMetadata.history}")
    }

    fun isOccupiedByColor(square: Square, color: Color) = this[square]?.color == color

    fun updatedWithoutValidation(move: Move): Board {
        val (newState, moveMetadata) = applyMove(move)
        return Board(newState, historyMetadata + moveMetadata)
    }

    fun updated(move: Move): Board {
        // this both validates the move is legal
        // and also replaces special moves that were parsed as basic moves
        val legalMove = legalMoves.find { it == move }
            ?: throw Exception("Unable to replay illegal move $move\n History: ${historyMetadata.history}")
        return updatedWithoutValidation(legalMove)
    }

    private fun applyMove(move: Move): Pair<Array<Array<Piece?>>,MoveMetadata> {
        val movingPiece = get(move.from) ?: throw Error("Invalid move, there is no piece on ${move.from}")
        var captured: Piece? = state[move.to.rank.index][move.to.file.index]
        val newState = state.map { it.copyOf() }.toTypedArray()
        newState[move.from.rank.index][move.from.file.index] = null
        newState[move.to.rank.index][move.to.file.index] = movingPiece

        when (move) {
            is Move.EnPassant -> {
                captured = state[move.captured.rank.index][move.captured.file.index]
                newState[move.captured.rank.index][move.captured.file.index] = null
            }
            is Move.Castle -> {
                newState[move.rookMove.from.rank.index][move.rookMove.from.file.index] = null
                newState[move.rookMove.to.rank.index][move.rookMove.to.file.index] = Piece(Rook, movingPiece.color)
            }
            is Move.Promotion -> {
                newState[move.to.rank.index][move.to.file.index] = Piece(move.promotion, movingPiece.color)
            }
        }

        return Pair(newState, MoveMetadata(move, movingPiece.kind, captured))
    }

    fun positionEquals(other: Board): Boolean = other.state.contentDeepEquals(this.state)
    
    companion object {
        fun parseHistory(moves: String): Board {
            return replayMoves(Board(), Move.parseMany(moves))
        }

        private fun replayMoves(board: Board, moves: List<Move>): Board {
            val nextMove = moves.firstOrNull()
            return if (nextMove != null) {
                replayMoves(board.updated(nextMove), moves.drop(1))
            } else board
        }

        val SQUARES: List<Square> by lazy {
            File.values().flatMap { file ->
                Rank.values().map { rank ->
                    Square(file, rank)
                }
            }
        }

        private val INITIAL_STATE: Array<Array<Piece?>> = arrayOf(
            arrayOf<Piece?>(
                Piece(Rook, Light),
                Piece(Knight, Light),
                Piece(Bishop, Light),
                Piece(Queen, Light),
                Piece(King, Light),
                Piece(Bishop, Light),
                Piece(Knight, Light),
                Piece(Rook, Light)
            ),
            arrayOf<Piece?>(
                Piece(Pawn, Light),
                Piece(Pawn, Light),
                Piece(Pawn, Light),
                Piece(Pawn, Light),
                Piece(Pawn, Light),
                Piece(Pawn, Light),
                Piece(Pawn, Light),
                Piece(Pawn, Light)
            ),
            arrayOf<Piece?>(null, null, null, null, null, null, null, null),
            arrayOf<Piece?>(null, null, null, null, null, null, null, null),
            arrayOf<Piece?>(null, null, null, null, null, null, null, null),
            arrayOf<Piece?>(null, null, null, null, null, null, null, null),
            arrayOf<Piece?>(
                Piece(Pawn, Dark),
                Piece(Pawn, Dark),
                Piece(Pawn, Dark),
                Piece(Pawn, Dark),
                Piece(Pawn, Dark),
                Piece(Pawn, Dark),
                Piece(Pawn, Dark),
                Piece(Pawn, Dark)
            ),
            arrayOf<Piece?>(
                Piece(Rook, Dark),
                Piece(Knight, Dark),
                Piece(Bishop, Dark),
                Piece(Queen, Dark),
                Piece(King, Dark),
                Piece(Bishop, Dark),
                Piece(Knight, Dark),
                Piece(Rook, Dark)
            )
        )
    }
}

