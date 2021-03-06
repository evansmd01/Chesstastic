package chesstastic.engine.metadata

import chesstastic.engine.entities.Piece
import chesstastic.engine.entities.Square

data class PieceMetadata(val piece: Piece, val square: Square)
