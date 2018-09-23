package chesstastic.cli.view.pieces

object KingView: PieceView {
    override val drawing = listOf(
        "############",
        "### ++++ ###",
        "### |--| ###",
        "### \\__/ ###",
        "### //|\\ ###",
        "## //_|_\\ ##"
    )
}
