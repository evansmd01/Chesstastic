package chesstastic.cli.view.pieces

import chesstastic.cli.ConsoleColor
import chesstastic.engine.entities.*

interface PieceView {
    /**
     * Representation of a drawing, where any null item is transparent
     */
    val drawing: List<List<String?>>

    fun render(color: Color): List<List<String?>> {
        return drawing.map {
            val myColor = if(color == Color.Light) ConsoleColor.CYAN else ConsoleColor.PURPLE
            val firstNotNull = it.indexOfFirst { it != null }
            val lastNotNull = it.indexOfLast { it != null }
            val copy = it.toMutableList()
            if (firstNotNull != -1) {
                copy[firstNotNull] = myColor + copy[firstNotNull]
            }
            if(lastNotNull != -1) {
                copy[lastNotNull] = copy[lastNotNull] + ConsoleColor.RESET
            }
            copy
        }
    }

    companion object {
        fun render(piece: Piece): List<List<String?>> = when(piece) {
            is Pawn -> PawnView
            is Rook -> RookView
            is Bishop -> BishopView
            is Knight -> KnightView
            is King -> KingView
            is Queen -> QueenView
            else -> throw NotImplementedError()
        }.render(piece.color)
    }
}
