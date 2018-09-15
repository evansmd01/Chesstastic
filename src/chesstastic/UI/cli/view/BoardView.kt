package chesstastic.UI.cli.view

import chestastic.Engine.*

class BoardView(val output: (String) -> Unit) {
    fun render(board: Board) {
        val sb = StringBuilder()
        sb.appendln(fileLabels)
        sb.appendln(rankDivider)
        board.topDown().forEach { (rank, rankState) ->
            sb.appendln(renderRank(rank, rankState))
            sb.appendln(rankDivider)
        }
        sb.appendln(fileLabels)
        output(sb.toString())
    }



    companion object {
        private fun Board.topDown(): Iterable<Pair<Rank, Array<Piece?>>> =
            this.state.mapIndexed { index, arrayOfPieces ->
                Pair(Rank.fromIndex(index), arrayOfPieces)
            }.reversed()


        private val fileLabels = "     A   B   C   D   E   F   G   H"
        private val rankDivider = "   +---+---+---+---+---+---+---+---+"

        private fun renderRank(rank: Rank, state: Array<Piece?>): String =
            state.joinToString(separator = " | ", prefix = " $rank | ", postfix = " | $rank") {
                it?.let { renderPiece(it) } ?: " "
            }

        private fun renderPiece(piece: Piece): String = when(piece) {
            is Pawn -> select(piece.color, "♟", "♙")
            is Rook -> select(piece.color, "♜", "♖")
            is Knight -> select(piece.color, "♞", "♘")
            is Bishop -> select(piece.color, "♝", "♗")
            is Queen -> select(piece.color, "♛", "♕")
            is King -> select(piece.color, "♚", "♔")
        }

        private fun select(color: Color, light: String, dark: String): String =
            when (color) {
                Color.Light -> light
                else -> dark
            }
    }
}
