package chesstastic.engine.metadata

import chesstastic.engine.entities.Move
import chesstastic.engine.entities.Piece

/**
 * Contextual information about a move that was played
 */
data class MoveMetadata(val move: Move, val piece: Piece, val capturing: PieceMetadata?) {
    val pieceMetadata = PieceMetadata(piece, move.from)
}
