package chesstastic.engine.metadata

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Color.*

/**
 * Precomputed information about the state of the position
 * which can be used for many different purposes,
 * such as validating moves, declaring check, checkmate, or stalemate,
 * or for analyzing various heuristics to determine the favorability of the position
 *
 * This serves as an optimization to prevent wasting cycles
 * looping over the board to collect information separately for each purpose.
 *
 * This uses lots of mutable collections and does lots of scary, side-effecting by-ref mutations.
 * But it's all for the sake of minimizing cycles spent mapping, looping, and copying
 */
data class BoardMetadata(
    val board: Board,
    val squares: Map<Square, SquareMetadata>,
    val lightPlayer: PlayerMetadata,
    val darkPlayer: PlayerMetadata
) {
    private val currentPlayer = if (board.historyMetadata.currentTurn == Light) lightPlayer else darkPlayer
    private fun isInCheck(color: Color): Boolean {
        val king = if (color == Light) lightPlayer.king else darkPlayer.king
        val kingSquare = squares[king.square] ?: throw Exception("Could not find king square")
        return kingSquare.isAttackedBy.any()
    }

    val legalMoves by lazy { currentPlayer.moves.all.map{ it.move } }
    val isCheck = isInCheck(board.historyMetadata.currentTurn)
    val isCheckmate = isCheck && currentPlayer.moves.all.isEmpty()
    val isStalemate by lazy {
        val inactivityLimit = 100 // TODO: Verify the "50 move rule" counts 1 move as 2 players moving. i.e. a turn.
        val inactivityLimitReached = board.historyMetadata.inactivityCount >= inactivityLimit
        val noLegalMoves = currentPlayer.moves.all.isEmpty() && !isCheck
        val remainingPieces = (lightPlayer.allPieces + darkPlayer.allPieces).size
        inactivityLimitReached || noLegalMoves || remainingPieces <= 2
    }

    val isGameOver = isStalemate || isCheckmate


    val attackedPieces: List<SquareMetadata> = squares.values.filter {
        it.occupant != null && it.isAttackedBy.isNotEmpty()
    }
}
