package chesstastic.engine.entities.metadata.moves

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Direction.HorizontalAndVertical.*
import chesstastic.engine.entities.Direction.Diagonal.*
import chesstastic.engine.entities.metadata.moves.LineMoves.Continuation

interface LineMoves {
    fun calculate(
        fromSquare: Square,
        process: (Move) -> Continuation
    )

    enum class Continuation {
        Stop, KeepGoing
    }
}

interface LineMovesIn<T: Enum<T>>: LineMoves {
    fun Square.transform(direction: T): Square?
    val directions: Array<T>

    override fun calculate(
        fromSquare: Square,
        process: (Move) -> Continuation
    ) {
        directions.forEach { moveInDirection(it, fromSquare, fromSquare, process) }
    }

    private fun moveInDirection(
        direction: T,
        fromSquare: Square,
        previousSquare: Square,
        process: (Move) -> Continuation
    ) {
        val toSquare = previousSquare.transform(direction)
        if(toSquare != null) {
            val then = process(Move.Basic(fromSquare, toSquare))
            if (then == Continuation.KeepGoing) {
                moveInDirection(direction, fromSquare, toSquare, process)
            }
        }
    }
}

object RookAndQueenMoves: LineMovesIn<Direction.HorizontalAndVertical> {
    override val directions = Direction.HorizontalAndVertical.values()
    override fun Square.transform(direction: Direction.HorizontalAndVertical): Square? = when (direction) {
        U -> this.transform(0, 1)
        D -> this.transform(0, -1)
        L -> this.transform(-1, 0)
        R -> this.transform(1, 0)
    }
}

object BishopAndQueenMoves: LineMovesIn<Direction.Diagonal> {
    override val directions = Direction.Diagonal.values()
    override fun Square.transform(direction: Direction.Diagonal): Square? = when (direction) {
        UL -> this.transform(-1, 1)
        UR -> this.transform(1, 1)
        DL -> this.transform(-1, -1)
        DR -> this.transform(1, -1)
    }
}

