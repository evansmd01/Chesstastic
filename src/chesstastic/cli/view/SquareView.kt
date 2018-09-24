package chesstastic.cli.view

import chesstastic.cli.view.pieces.PieceView
import chesstastic.engine.entities.Color
import chesstastic.engine.entities.Piece

object SquareView {
    const val SQUARE_HEIGHT = 6
    const val SQUARE_WIDTH = 12

    fun render(bgColor: Color, piece: Piece?): List<List<String>> {
        val bgChar = if (bgColor == Color.Light) " " else "."
        val square = (0..(SQUARE_HEIGHT - 1)).map { _ -> (0..(SQUARE_WIDTH - 1)).map { bgChar }.toMutableList() }
        return if (piece != null) {
            // lay the piece view over the background view
            // any items in the piece arrays that are # will act as transparent
            val pieceView = PieceView.render(piece)
            square.mapIndexed { lineIndex, line ->
                val pieceLine = pieceView[lineIndex]
                line.mapIndexed { charIndex, char ->
                    val pieceChar = pieceLine[charIndex]
                    if (pieceChar != "#") pieceChar else char
                }
            }
        } else square
    }
}
