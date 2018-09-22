package chesstastic.engine.entities

import chesstastic.engine.calculators.BoardCalculator
import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.entities.Color.*


class Board(
    private val state: Array<Array<Piece?>>,
    val history: List<Move>,
    val turn: Color,
    private val inactivityCounter: Int
) {
    operator fun get(coord: Square): Piece? = state[coord.rank.index][coord.file.index]

    val legalMoves by lazy { BoardCalculator.legalMoves(this, turn) }

    val isCheck by lazy { isInCheck(turn) }
    fun isInCheck(color: Color) = BoardCalculator.isKingInCheck(color, this)
    val isCheckmate by lazy { legalMoves.count() == 0 && isCheck }
    private val inactivityLimit = 500
    private val remainingPieces by lazy { state.sumBy { it.filterNotNull().count() } }
    val isStalemate by lazy { inactivityCounter >= inactivityLimit || (legalMoves.count() == 0 && !isCheck) || remainingPieces <= 2 }
    val isGameOver by lazy { isCheckmate || isStalemate }

    fun isSquareAttacked(square: Square, attacker: Color) = BoardCalculator.isSquareAttacked(square, attacker, this)

    fun kingSquare(color: Color): Square  {
        return SQUARES.firstOrNull() { square ->
            val piece = this[square]
            when {
                piece?.kind == King && piece.color == color -> true
                else -> false
            }
        } ?: throw Error("King is missing from the board.\nHistory: ${this.history.joinToString(separator = ",")}")
    }

    fun isOccupiedByColor(square: Square, color: Color) = this[square]?.color == color

    fun updated(move: Move): Board {
        val newState = applyMove(move)
        val newHistory = history + move
        val wasCapture = false // TODO: implement this, or game will always end on 30th move
        val newInactivityCount = if (wasCapture) 0 else inactivityCounter + 1
        return Board(newState, newHistory, turn.opposite, newInactivityCount)
    }

    private fun applyMove(move: Move): Array<Array<Piece?>> {
        val movingPiece = get(move.from) ?: throw Error("Invalid move, there is no piece on ${move.from}")

        val newState = state.map { it.copyOf() }.toTypedArray()
        newState[move.from.rank.index][move.from.file.index] = null
        newState[move.to.rank.index][move.to.file.index] = movingPiece

        if (move is Move.EnPassant) {
            newState[move.captured.rank.index][move.captured.file.index] = null
        } else if (move is Move.Castle) {
            newState[move.rook.from.rank.index][move.rook.from.file.index] = null
            newState[move.rook.to.rank.index][move.rook.to.file.index] = Piece(Rook, movingPiece.color)
        } else if (move is Move.Promotion) {
            newState[move.to.rank.index][move.to.file.index] = Piece(move.promotion, movingPiece.color)
        }

        return newState
    }

    fun positionEquals(other: Board): Boolean = other.state.contentDeepEquals(this.state)
    
    companion object {
        fun createNew(): Board = Board(InitialState, listOf(), Color.Light, 0)

        fun parseHistory(moves: String): Board {
            return replayMoves(createNew(), Move.parseMany(moves))
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

        private val InitialState: Array<Array<Piece?>> = arrayOf(
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

