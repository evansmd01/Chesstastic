package chesstastic.cli.view.pieces

import chesstastic.util.ConsoleColor
import chesstastic.engine.entities.*
import chesstastic.engine.entities.PieceKind.*

interface PieceView {
    /**
     * Representation of a drawing, where any null item is transparent
     */
    val drawing: List<String>

    fun render(color: Color): List<List<String>> {
        return drawing.map { line ->
            val stringChars = line.map { c -> c.toString() }
            val myColor = if(color == Color.Light) ConsoleColor.CYAN else ConsoleColor.PURPLE
            val firstNotHash = stringChars.indexOfFirst { it != "#" }
            val lastNotHash = stringChars.indexOfLast { it != "#" }
            val copy = stringChars.toMutableList()
            if (firstNotHash != -1) {
                copy[firstNotHash] = myColor + copy[firstNotHash]
            }
            if(lastNotHash != -1) {
                copy[lastNotHash] = copy[lastNotHash] + ConsoleColor.RESET
            }
            copy
        }
    }

    companion object {
        fun render(piece: Piece): List<List<String?>> = when(piece.kind) {
            Pawn -> PawnView
            Rook -> RookView
            Bishop -> BishopView
            Knight -> KnightView
            King -> KingView
            Queen -> QueenView
        }.render(piece.color)
    }
}
