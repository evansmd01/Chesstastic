package chesstastic.cli.view.pieces

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
