package chesstastic.cli.view.messages

object StalemateView: MessageView {
    override val drawing = """
        |    *****  ******     ***      **      ******  **       **      ***     ******  ******  |
        |  **         **      ** **     **      **      ** ** ** **     ** **      **    **      |
        |    ****     **     *******    **      *****   **   *   **    *******     **    *****   |
        |        **   **    **     **   **      **      **       **   **     **    **    **      |
        |   *****     **   **       **  ******  ******  **       **  **       **   **    ******  |
    """.trimIndent()
}
