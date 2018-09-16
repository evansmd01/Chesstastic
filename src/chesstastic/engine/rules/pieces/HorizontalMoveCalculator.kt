package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*

interface HorizontalMoveCalculator: StraightLineMoveCalculator<HorizontalDirection> {
    override fun directions() = HorizontalDirection.values()
    override fun Square.transform(direction: HorizontalDirection): Square? = when (direction) {
        HorizontalDirection.U -> this.transform(0, 1)
        HorizontalDirection.D -> this.transform(0, -1)
        HorizontalDirection.L -> this.transform(-1, 0)
        HorizontalDirection.R -> this.transform(1, 0)
    }
}

enum class HorizontalDirection {
    U,D,L,R
}
