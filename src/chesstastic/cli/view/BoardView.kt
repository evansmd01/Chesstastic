package chesstastic.cli.view

import chesstastic.engine.entities.*

class BoardView {
    companion object {
        private val fileLabels = "     A   B   C   D   E   F   G   H\n"
        private val rankDivider = "   +---+---+---+---+---+---+---+---+\n"

        fun render(board: Board): String =
            Rank.values().reversed().joinToString(
                    separator = rankDivider,
                    prefix = fileLabels + rankDivider,
                    postfix = rankDivider + fileLabels)
            { rank ->
                File.values().joinToString(
                        separator = " | ",
                        prefix = " $rank | ",
                        postfix = " | $rank\n")
                { file ->
                    board[Square(file, rank)]?.let { renderPiece(it) } ?: " "
                }
            }

        private fun renderPiece(piece: Piece): String = when(piece) {
            is Pawn -> select(piece.color, "♙")
            is Rook -> select(piece.color, "♖")
            is Knight -> select(piece.color, "♘")
            is Bishop -> select(piece.color, "♗")
            is Queen -> select(piece.color, "♕")
            is King -> select(piece.color, "♔")
            else -> throw NotImplementedError()
        }

        private fun select(color: Color, piece: String): String =
            when (color) {
                Color.Light -> "${ConsoleColor.CYAN}$piece${ConsoleColor.RESET}"
                else -> "${ConsoleColor.PURPLE}$piece${ConsoleColor.RESET}"
            }
    }
}
