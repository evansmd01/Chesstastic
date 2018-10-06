package chesstastic.util

import chesstastic.engine.entities.*
import chesstastic.engine.entities.PieceKind.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.metadata.CastleMetadata
import chesstastic.engine.metadata.HistoryMetadata

/*
COPY PASTE THAT SHIZ!

                val board = Snapshot.parse("""
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p|p|p|p|p|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |P|P|P|P|P|P|P|P|
                    |R|N|B|Q|K|B|N|R|
                """.trimIndent(), turn = Color.Light)

 */
object Snapshot {
    fun parse(value: String, turn: Color, allowCastle: Boolean = false): Board {
        val state = value.trim()
            .split("\n")
            .reversed()
            .asSequence()
            .map{ it.trim() }
            .filter { it.isNotEmpty() }
            .map { rank ->
                rank.split("|")
                    .asSequence()
                    .filter { it.isNotEmpty() }
                    .map { pieceFromString(it) }
                    .toList()
                    .toTypedArray()
            }
            .toList()
            .toTypedArray()
        return Board(state, historyMetadata = HistoryMetadata.EMPTY.copy(
            currentTurn = turn,
            lightCastling = CastleMetadata.LIGHT.copy(kingHasMoved = !allowCastle),
            darkCastling = CastleMetadata.DARK.copy(kingHasMoved = !allowCastle)
        ))
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
