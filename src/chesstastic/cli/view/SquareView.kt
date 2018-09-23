package chesstastic.cli.view

import chesstastic.cli.view.pieces.PieceView
import chesstastic.engine.entities.Color
import chesstastic.engine.entities.Piece

object SquareView {
    fun render(bgColor: Color, piece: Piece?): List<List<String>> {
        val bgChar = if (bgColor == Color.Light) " " else "."
        val square = (0..5).map { (0..11).map { bgChar }.toMutableList() }
        return if (piece != null) {
            // lay the piece view over the background view
            // any items in the piece arrays that are # will act as transparent
            val pieceView = PieceView.render(piece)
            square.mapIndexed { lineIndex, line ->
                val pieceLine = pieceView[lineIndex]
                line.mapIndexed { charIndex, char ->
                    val pieceChar = pieceLine[charIndex]
                    if (pieceChar != "#") pieceChar ?: char else char
                }
            }
        } else square
    }
}
