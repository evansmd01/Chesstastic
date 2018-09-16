package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*

interface StraightLineMoveCalculator<T>: PieceMoveCalculator {
    fun isCorrectPiece(piece: Piece): Boolean
    fun Square.transform(direction: T): Square?
    fun directions(): Array<T>

    override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> =
        directions()
            .flatMap { movesHeading(it, color, fromSquare, board) }
            .map { Move.Basic(fromSquare, it) }

    override fun timesSquareIsAttacked(target: Square, attacker: Color, board: Board): Int {
        // The recursion return the squares in reverse order,
        // which is handy since only the last move in any direction could be a piece
        val friendlyColor = attacker.opposite
        return directions().mapNotNull {
            movesHeading(it, friendlyColor, target, board).firstOrNull()
        }.count {
            val piece = board[it]
            piece != null && isCorrectPiece(piece)
        }
    }

    private fun movesHeading(
        direction: T,
        color: Color,
        fromSquare: Square,
        board: Board): Iterable<Square> {
        val nextSquare = fromSquare.transform(direction)
        val occupant = nextSquare?.let { board[it] }
        return if(nextSquare != null && occupant?.color != color) {
            if(occupant != null) {
                // end recursion, we've found a piece of the opposite player.
                listOf(nextSquare)
            } else {
                // empty square, continue recursively finding moves
                movesHeading(direction, color, nextSquare, board) + nextSquare
            }
        } else listOf() // end recursion, not including this friendly square
    }
}
