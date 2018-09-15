package chesstastic.engine.entities

import chesstastic.engine.rules.MoveCalculator

typealias BoardState = Array<Array<Piece?>>

class Board(
        private val state: BoardState,
        private val history: List<BoardState>,
        val turn: Color
) {
    operator fun get(file: File, rank: Rank): Piece? = state[rank.index][file.index]

    private val validMoves by lazy { MoveCalculator(this).validMoves }

    val isCheckmate = validMoves.count() == 0

    fun update(move: Move): Board? {
        if (move !in validMoves)
            return null

        val movingPiece = get(move.from.file, move.from.rank)
        val newState = state.mapIndexed { rankIndex, ranks ->
            ranks.mapIndexed { fileIndex, piece ->
                when {
                    move.from.indexEquals(fileIndex, rankIndex) -> null
                    move.to.indexEquals(fileIndex, rankIndex) -> movingPiece
                    else -> piece
                }
            }.toTypedArray()
        }.toTypedArray()
        val newHistory = history + listOf(state)
        return Board(newState, newHistory, turn.opposite)
    }
    
    companion object {
        fun createNew(): Board = Board(InitialState, listOf(), Color.Light)
        private val InitialState: BoardState = arrayOf(
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

