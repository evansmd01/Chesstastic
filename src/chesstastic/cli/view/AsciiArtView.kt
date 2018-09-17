package chesstastic.cli.view

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Color.*

class AsciiArtView {
    companion object {
        fun render(board: Board): String {
            val sb = StringBuilder()
            val fileLabels = File.values().joinToString(separator = "           ", prefix = "        ")
            sb.appendln(fileLabels)
            sb.appendln()
            Rank.values().reversed().forEach { rank ->
                val squareViews = File.values().map { file ->
                    val bgColor = if ((rank.index + file.index) % 2 == 0) Light else Dark
                    val piece = board[Square(file, rank)]
                    SquareView.render(bgColor, piece)
                }
                // line by line, scroll through each squareView and append the corresponding line
                (0..5).forEach { lineIndex ->
                    val rankLabel = if(lineIndex == 3) "${rank.index + 1}  " else "   "
                    sb.append(rankLabel)
                    squareViews.forEach {
                        it[lineIndex].forEach { sb.append(it) }
                    }
                    sb.appendln("  $rankLabel")
                }
            }
            sb.appendln()
            sb.appendln(fileLabels)
            return sb.toString()
        }
    }
}

class SquareView {
    companion object {
        fun render(bgColor: Color, piece: Piece?): List<List<String>> {
            val bgChar = if (bgColor == Light) " " else "."
            val square = (0..5).map { r -> (0..11).map { c -> bgChar }.toMutableList() }
            return if (piece != null) {
                // lay the piece view over the background view
                // any items in the piece arrays that are null will act as transparent
                val pieceView = PieceView.render(piece)
                square.mapIndexed { lineIndex, line ->
                    val pieceLine = pieceView[lineIndex]
                    line.mapIndexed { charIndex, char -> pieceLine[charIndex] ?: char }
                }
            } else square
        }
    }
}

interface PieceView {
    /**
     * Representation of a drawing, where any null item is transparent
     */
    val drawing: List<List<String?>>

    fun render(color: Color): List<List<String?>> {
        return drawing.map {
            val myColor = if(color == Light) ConsoleColor.CYAN else ConsoleColor.PURPLE
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

class PawnView {
    companion object: PieceView {
        override val drawing = listOf<MutableList<String?>>(
            mutableListOf(null, null, null, null, null, null, null, null, null, null, null, null),
            mutableListOf(null, null, null, null, " ", "_", "_", " ", null, null, null, null),
            mutableListOf(null, null, null, " ", "(", " ", " ", ")", " ", null, null, null),
            mutableListOf(null, null, null, null, " ", ")", "(", " ", null, null, null, null),
            mutableListOf(null, null, null, " ", "/", "_", "_", "\\", " ", null, null, null),
            mutableListOf(null, null, null, null, null, null, null, null, null, null, null, null)
        )
    }
}

class RookView {
    companion object: PieceView {
        override val drawing = listOf<MutableList<String?>>(
            mutableListOf(null, null, null, null, null, null, null, null, null, null, null, null),
            mutableListOf(null, null, " ", "|", "_", "|", "|", "_", "|", " ", null, null),
            mutableListOf(null, null, null, " ", "|", " ", " ", "|", " ", null, null, null),
            mutableListOf(null, null, null, " ", "|", " ", " ", "|", " ", null, null, null),
            mutableListOf(null, null, null, " ", "|", " ", " ", "|", " ", null, null, null),
            mutableListOf(null, null, " ", "/", "_", "_", "_", "_", "\\", " ", null, null)
        )
    }
}

class KnightView {
    companion object: PieceView {
        override val drawing = listOf<MutableList<String?>>(
            mutableListOf(null, null, null, null, null, null, null, null, null, null, null, null),
            mutableListOf(null, null, null, " ", "_", ",", ",", "~", " ", null, null, null),
            mutableListOf(null, null, " ", "\"", "-", " ", " ", "\\", "~", " ", null, null),
            mutableListOf(null, null, null, null, " ", "|", " ", "|", "~", " ", null, null),
            mutableListOf(null, null, null, null, " ", "|", " ", "|", " ", null, null, null),
            mutableListOf(null, null, null, " ", "/", "_", "_", "_", "\\", " ", null, null)
        )
    }
}

class BishopView {
    companion object: PieceView {
        override val drawing = listOf<MutableList<String?>>(
            mutableListOf(null, null, null, null, null, null, null, null, null, null, null, null),
            mutableListOf(null, null, null, null, " ", "/", "\\", " ", null, null, null, null),
            mutableListOf(null, null, null, " ", "\\", " ", " ", "/", " ", null, null, null),
            mutableListOf(null, null, null, null, " ", "|", "|", " ", null, null, null, null),
            mutableListOf(null, null, null, null, " ", "|", "|", " ", null, null, null, null),
            mutableListOf(null, null, null, " ", "/", "_", "_", "\\", " ", null, null, null)
        )
    }
}

class QueenView {
    companion object: PieceView {
        override val drawing = listOf<MutableList<String?>>(
            mutableListOf(null, null, null, " ", "w", "W", "W", "w", " ", null, null, null),
            mutableListOf(null, null, null, " ", "\\", "\\", "/", "/", " ", null, null, null),
            mutableListOf(null, null, null, null, " ", "|", "|", " ", null, null, null, null),
            mutableListOf(null, null, null, " ", "/", "/", "/", "\\", " ", null, null, null),
            mutableListOf(null, null, " ", "/", "/", "/", " ", " ", "\\", " ", null, null),
            mutableListOf(null, " ", "/", "/", "/", "_", "_", "_", "_", "\\", " ", null)
        )
    }
}

class KingView {
    companion object: PieceView {
        override val drawing = listOf<MutableList<String?>>(
            mutableListOf(null, null, null, null, null, null, null, null, null, null, null, null),
            mutableListOf(null, null, null, " ", "+", "+", "+", "+", " ", null, null, null),
            mutableListOf(null, null, null, " ", "|", "-", "-", "|", " ", null, null, null),
            mutableListOf(null, null, null, " ", "\\", "_", "_", "/", " ", null, null, null),
            mutableListOf(null, null, null, " ", "/", "/", "|", "\\", " ", null, null, null),
            mutableListOf(null, null, " ", "/", "/", "_", "|", "_", "\\", " ", null, null)
        )
    }
}
