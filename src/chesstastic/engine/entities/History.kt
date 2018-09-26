package chesstastic.engine.entities

/**
 * Reverse linked list of moves.
 * The only reasons to access history are to check if the most recent move allows an en passant response
 * or to check for threefold repetitions causing a draw at the endgame.
 * So it's most efficient to start at the most recent and work backwards through history
 */
data class History(val mostRecent: Move?, val previous: History?) {
    fun first(): Move? = if (previous?.mostRecent != null) previous.first() else mostRecent

    override fun toString(): String =
        mostRecent?.let { "${previous?.toString() ?: ""}$it " } ?: ""
}
