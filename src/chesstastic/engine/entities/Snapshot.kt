package chesstastic.engine.entities

import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.entities.Color.*
import chesstastic.util.transformIf

object Snapshot {
    fun parse(value: String, turn: Color): Board {
        val state = value.trim()
            .split("\n")
            .reversed()
            .map{ it.trim() }
            .filter { it.isNotEmpty() }
            .map {
                it.split("|")
                    .filter { it.isNotEmpty() }
                    .map { pieceFromString(it) }
                    .toTypedArray()
            }
            .toTypedArray()
        return Board(state, history = emptyList(), turn = turn, inactivityCounter = 0)
    }

    fun from(board: Board): String =
        Rank.values().reversed().joinToString(separator = "\n") { rank ->
            File.values().joinToString(separator = "|", prefix = "|", postfix = "|") { file ->
                val piece = board[Square(file, rank)]
                stringRepresentation(piece)
            }
        }

    private fun stringRepresentation(piece: Piece?): String = when {
        piece?.kind == Pawn -> "p"
        piece?.kind == Rook -> "r"
        piece?.kind == Knight -> "n"
        piece?.kind == Bishop -> "b"
        piece?.kind == Queen -> "q"
        piece?.kind == King -> "k"
        else -> " "
    }.transformIf({ piece?.color == Light }, { it.toUpperCase() })

    private fun pieceFromString(value: String): Piece? = when (value) {
        "p" -> Piece(Pawn, Dark)
        "P" -> Piece(Pawn, Light)
        "r" -> Piece(Rook, Dark)
        "R" -> Piece(Rook, Light)
        "n" -> Piece(Knight, Dark)
        "N" -> Piece(Knight, Light)
        "b" -> Piece(Bishop, Dark)
        "B" -> Piece(Bishop, Light)
        "q" -> Piece(Queen, Dark)
        "Q" -> Piece(Queen, Light)
        "k" -> Piece(King, Dark)
        "K" -> Piece(King, Light)
        else -> null
    }
}
