package chesstastic.engine.entities

import chesstastic.engine.rules.MoveCalculator


class Board(
    private val state: Array<Array<Piece?>>,
    val history: List<Move>,
    val turn: Color,
    private val inactivityCounter: Int
) {
    operator fun get(coord: Square): Piece? = state[coord.rank.index][coord.file.index]

    private val legalMoves by lazy { MoveCalculator.legalMoves(this) }

    val isCheck by lazy { MoveCalculator.isKingInCheck(turn, this) }
    val isCheckmate by lazy { legalMoves.count() == 0 && isCheck }
    val isStalemate by lazy { inactivityCounter >= 60 || legalMoves.count() == 0 && !isCheck }

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
            newState[move.rook.to.rank.index][move.rook.to.file.index] = movingPiece
        } else if (move is Move.Promotion) {
            newState[move.to.rank.index][move.to.file.index] = move.promotion
        }

        return newState
    }
    
    companion object {
        fun createNew(): Board = Board(InitialState, listOf(), Color.Light, 0)

        fun parseHistory(history: String): Board {
            return applyHistoryRecursive(createNew(), Move.parseMany(history))
        }

        private fun applyHistoryRecursive(board: Board, history: List<Move>): Board {
            val nextMove = history.firstOrNull()
            return if (nextMove != null) {
                applyHistoryRecursive(board.updated(nextMove), history.drop(1))
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
                        Rook(Color.Light),
                        Knight(Color.Light),
                        Bishop(Color.Light),
                        Queen(Color.Light),
                        King(Color.Light),
                        Bishop(Color.Light),
                        Knight(Color.Light),
                        Rook(Color.Light)
                ),
                arrayOf<Piece?>(
                        Pawn(Color.Light),
                        Pawn(Color.Light),
                        Pawn(Color.Light),
                        Pawn(Color.Light),
                        Pawn(Color.Light),
                        Pawn(Color.Light),
                        Pawn(Color.Light),
                        Pawn(Color.Light)
                ),
                arrayOf<Piece?>(null, null, null, null, null, null, null, null),
                arrayOf<Piece?>(null, null, null, null, null, null, null, null),
                arrayOf<Piece?>(null, null, null, null, null, null, null, null),
                arrayOf<Piece?>(null, null, null, null, null, null, null, null),
                arrayOf<Piece?>(
                        Pawn(Color.Dark),
                        Pawn(Color.Dark),
                        Pawn(Color.Dark),
                        Pawn(Color.Dark),
                        Pawn(Color.Dark),
                        Pawn(Color.Dark),
                        Pawn(Color.Dark),
                        Pawn(Color.Dark)
                ),
                arrayOf<Piece?>(
                        Rook(Color.Dark),
                        Knight(Color.Dark),
                        Bishop(Color.Dark),
                        Queen(Color.Dark),
                        King(Color.Dark),
                        Bishop(Color.Dark),
                        Knight(Color.Dark),
                        Rook(Color.Dark)
                )
        )
    }
}
