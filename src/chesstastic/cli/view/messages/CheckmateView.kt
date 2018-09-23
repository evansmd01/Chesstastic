package chesstastic.cli.view.messages

object CheckmateView: MessageView {
    override val drawing = """
        |    ****  **  **  ******    ****  **    **  **       **      ***     ******  ******  |
        |  **      **  **  **      **      **  **    ** ** ** **     ** **      **    **      |
        |  **      ******  *****   **      ****      **   *   **    *******     **    *****   |
        |  **      **  **  **      **      **  **    **       **   **     **    **    **      |
        |    ****  **  **  ******    ****  **    **  **       **  **       **   **    ******  |
    """.trimIndent()
}
