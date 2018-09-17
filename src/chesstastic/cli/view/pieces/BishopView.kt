package chesstastic.cli.view.pieces

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
