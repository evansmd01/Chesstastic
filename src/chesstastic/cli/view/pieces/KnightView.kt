package chesstastic.cli.view.pieces

class KnightView {
    companion object: PieceView {
        override val drawing = listOf<MutableList<String?>>(
            mutableListOf(null, null, null, null, null, null, null, null, null, null, null, null),
            mutableListOf(null, null, null, " ", "_", "|", "\\", " ", null, null, null, null),
            mutableListOf(null, null, " ", "/", "o", " ", "\\", "\\", " ", null, null, null),
            mutableListOf(null, " ", "(", "_", ",", " ", " ", "|", "|", " ", null, null),
            mutableListOf(null, null, null, " ", "|", "_", "_", "\\", "|", " ", null, null, null),
            mutableListOf(null, null, " ", "/", "_", "_", "_", "_", "\\", " ", null, null, null)
        )
    }
}
