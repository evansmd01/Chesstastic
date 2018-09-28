package chesstastic.engine.entities

import chesstastic.engine.calculators.MetadataCalculator
import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.metadata.*


class Board(
    private val state: Array<Array<Piece?>> = INITIAL_STATE,
    val historyMetadata: HistoryMetadata = HistoryMetadata.EMPTY
) {
    operator fun get(coord: Square): Piece? = state[coord.rank.index][coord.file.index]

    val metadata by lazy { MetadataCalculator.calculate(this) }

    fun updatedWithoutValidation(move: Move): Board {
        val (newState, moveMetadata) = applyMove(move)
        return Board(newState, historyMetadata + moveMetadata)
    }

    fun updated(move: Move): Board {
        // this both validates the move is legal
        // and also replaces special moves that were parsed as basic moves
        val legalMove = metadata.legalMoves.find { it == move }
            ?: throw Exception("Unable to replay illegal move $move\n History: ${historyMetadata.history}")
        return updatedWithoutValidation(legalMove)
    }

    private fun applyMove(move: Move): Pair<Array<Array<Piece?>>, MoveMetadata> {
        val movingPiece = get(move.from) ?: throw Error("Invalid move, there is no piece on ${move.from}")
        var captured: PieceMetadata? = state[move.to.rank.index][move.to.file.index]?.let { PieceMetadata(it, move.to) }
        val newState = state.map { it.copyOf() }.toTypedArray()
        newState[move.from.rank.index][move.from.file.index] = null
        newState[move.to.rank.index][move.to.file.index] = movingPiece

        when (move) {
            is Move.EnPassant -> {
                captured = PieceMetadata(Piece(Pawn, movingPiece.color.opposite), move.captured)
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

        return Pair(newState, MoveMetadata(move, movingPiece, captured, null))
    }

    fun positionEquals(other: Board): Boolean = other.state.contentDeepEquals(this.state)

    override fun toString(): String = historyMetadata.history.toString()

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

        val SQUARES: List<Square> =
            File.values().flatMap { file ->
                Rank.values().map { rank ->
                    Square(file, rank)
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

