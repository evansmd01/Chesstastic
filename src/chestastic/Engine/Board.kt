package chestastic.Engine


class Board(val state: Array<Array<Piece?>> = InitialState) {
    operator fun get(coord: Coordinate): Piece? = state[coord.rank.index][coord.file.index]

    fun move(from: Coordinate, to: Coordinate): Board {
        val movingPiece = get(from)
        val newState = state.mapIndexed { rankIndex, ranks ->
            ranks.mapIndexed { fileIndex, piece ->
                when {
                    from.indexEquals(fileIndex, rankIndex) -> null
                    to.indexEquals(fileIndex, rankIndex) -> movingPiece
                    else -> piece
                }
            }.toTypedArray()
        }.toTypedArray()
        return Board(newState)
    }
    
    companion object {
        private val InitialState = arrayOf(
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

