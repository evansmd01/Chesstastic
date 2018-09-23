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
        val myColor = if(color == Color.Light) ConsoleColor.CYAN else ConsoleColor.PURPLE
        return drawing.map { line ->
            line.map { c -> c.toString() }
                .map {
                    if (it != "#") {
                        myColor + it + ConsoleColor.RESET
                    } else it
                }
        }
    }

    companion object {
        fun render(piece: Piece): List<List<String>> = when(piece.kind) {
            Pawn -> PawnView
            Rook -> RookView
            Bishop -> BishopView
            Knight -> KnightView
            King -> KingView
            Queen -> QueenView
        }.render(piece.color)
    }
}
