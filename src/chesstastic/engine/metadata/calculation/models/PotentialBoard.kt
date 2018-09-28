package chesstastic.engine.metadata.calculation.models

import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Color
import chesstastic.engine.entities.Piece
import chesstastic.engine.entities.Square
import chesstastic.engine.metadata.BoardMetadata
import chesstastic.engine.metadata.PlayerMetadata
import chesstastic.engine.metadata.SquareMetadata

/**
 * Helper model for holding a bunch of mutable state while aggregating everything
 * during the big board metadata algorithm.
 *
 * Outside of this algorithm, I want the board metadata to be immutable,
 * so these are separate models for holding mutable collections
 */
data class PotentialBoard(
    private val board: Board,
    val squareMetadata: MutableMap<Square, SquareMetadata>,
    val lightMoves: PotentialMoves,
    val darkMoves: PotentialMoves,
    val lightMetadata: PlayerMetadata,
    val darkMetadata: PlayerMetadata
) {
    val historyMetadata = board.historyMetadata

    val getPiece: (square: Square) -> Piece? = { board[it] }

    fun isNotSafeToMove(color: Color, toSquare: Square): Boolean {
        val squareMeta = squareMetadata[toSquare]
            ?: throw Exception("Can find square meta at $toSquare")
        return squareMeta.isAttackedBy.any { it.piece.color == color.opposite }
            || squareMeta.isSupportedBy.any { it.piece.color == color.opposite }
    }

    fun finalize() = BoardMetadata(
        board,
        squares = squareMetadata,
        lightPlayer = lightMetadata.copy(moves = lightMoves.finalize()),
        darkPlayer = darkMetadata.copy(moves = darkMoves.finalize())
    )
}
