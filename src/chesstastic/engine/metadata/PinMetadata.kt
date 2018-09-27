package chesstastic.engine.metadata

data class PinMetadata(val pinned: PieceMetadata, val by: PieceMetadata, val to: PieceMetadata)
