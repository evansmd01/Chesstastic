package chesstastic.cli.view.messages

object CheckView: MessageView {
    override val drawing = """
        |    ****  **  **  ******    ****  **    **  |
        |  **      **  **  **      **      **  **    |
        |  **      ******  ****    **      ****      |
        |  **      **  **  **      **      **  **    |
        |    ****  **  **  ******    ****  **    **  |
    """.trimIndent()
}
