package chesstastic.cli.view

import chesstastic.cli.view.pieces.PieceView
import chesstastic.engine.entities.Color
import chesstastic.engine.entities.Piece
import chesstastic.util.applyColor
import chesstastic.util.yellow

object SquareView {
    const val SQUARE_HEIGHT = 6
    const val SQUARE_WIDTH = 12

    fun shouldHightlight(charIndex: Int, lineIndex: Int): Boolean =
        (charIndex == 0 || charIndex == SQUARE_WIDTH - 1) ||
            (lineIndex == 0 || lineIndex == SQUARE_HEIGHT - 1)

    fun render(bgColor: Color, piece: Piece?, highlight: Boolean): List<List<String>> {
        val bgChar = if (bgColor == Color.Light) " " else "."
        val square = (0..(SQUARE_HEIGHT - 1)).map { _ -> (0..(SQUARE_WIDTH - 1)).map { bgChar }.toMutableList() }

        val pieceView = piece?.let { PieceView.render(it) }
        return square.mapIndexed { lineIndex, line ->
            val pieceLine = pieceView?.get(lineIndex)
            line.mapIndexed { charIndex, char ->
                val pieceChar = pieceLine?.get(charIndex)
                when {
                    pieceChar != null && pieceChar != "#" -> pieceChar
                    highlight && shouldHightlight(charIndex, lineIndex) -> ".".yellow()
                    else -> char
                }
            }
        }
    }
}
