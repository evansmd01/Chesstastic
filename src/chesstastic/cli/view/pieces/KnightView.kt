package chesstastic.cli.view.pieces

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
