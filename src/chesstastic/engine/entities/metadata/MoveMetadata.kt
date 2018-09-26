package chesstastic.engine.entities.metadata

import chesstastic.engine.entities.Move
import chesstastic.engine.entities.Piece
import chesstastic.engine.entities.PieceKind

/**
 * Contextual information about a move that was played
 */
data class MoveMetadata(val move: Move, val piece: Piece, val captured: PieceMetadata?)
