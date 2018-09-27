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
    val currentPlayer = if (board.historyMetadata.currentTurn == Light) lightPlayer else darkPlayer
    fun isInCheck(color: Color): Boolean {
        val king = if (color == Light) lightPlayer.king else darkPlayer.king
        return squares[king.square]?.isAttackedBy?.any() ?: false
    }

    val legalMoves by lazy { currentPlayer.moves.all.map{ it.move } }
    val isCheck by lazy { isInCheck(board.historyMetadata.currentTurn) }
    val isCheckmate by lazy { legalMoves.isEmpty() && isCheck }
    private val inactivityLimit = 100 // TODO: Verify the "50 move rule" defines a "move" as both players having taken a turn, hence 100 here.
    private val remainingPieces by lazy { (lightPlayer.allPieces + darkPlayer.allPieces).size }
    val isStalemate by lazy { board.historyMetadata.inactivityCount >= inactivityLimit || (legalMoves.count() == 0 && !isCheck) || remainingPieces <= 2 }
    val isGameOver by lazy { isStalemate || isCheckmate }
}
