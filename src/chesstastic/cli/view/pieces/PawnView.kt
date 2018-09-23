package chesstastic.cli.view.pieces

object PawnView: PieceView {
    override val drawing = listOf(
        "############",
        "#### __ ####",
        "### (  ) ###",
        "#### )( ####",
        "### /__\\ ###",
        "############"
    )
}
