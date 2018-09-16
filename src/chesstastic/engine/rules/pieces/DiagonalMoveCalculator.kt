package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*

interface DiagonalMoveCalculator: StraightLineMoveCalculator<DiagonalDirection> {
    override fun directions() = DiagonalDirection.values()
    override fun Square.transform(direction: DiagonalDirection) = when (direction) {
        DiagonalDirection.UL -> this.transform(-1, 1)
        DiagonalDirection.UR -> this.transform(1, 1)
        DiagonalDirection.DL -> this.transform(-1, -1)
        DiagonalDirection.DR -> this.transform(1, -1)
    }
}

enum class DiagonalDirection {
    UL, UR, DL, DR
}

