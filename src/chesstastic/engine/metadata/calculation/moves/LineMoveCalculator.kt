package chesstastic.engine.metadata.calculation.moves

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Direction.HorizontalAndVertical.*
import chesstastic.engine.entities.Direction.Diagonal.*
import chesstastic.engine.metadata.calculation.moves.LineMoveCalculator.Continuation
import chesstastic.engine.metadata.MoveMetadata
import chesstastic.engine.metadata.PieceMetadata

interface LineMoveCalculator {
    fun calculate(
        fromSquare: Square,
        piece: Piece,
        getPiece: (Square) -> Piece?,
        process: (move: MoveMetadata, previousCapture: PieceMetadata?) -> Continuation
    )

    enum class Continuation {
        Stop, KeepGoing
    }
}

interface LineMoveCalculatorIn<T: Enum<T>>: LineMoveCalculator {
    fun Square.transform(direction: T): Square?
    val directions: Array<T>

    override fun calculate(
        fromSquare: Square,
        piece: Piece,
        getPiece: (Square) -> Piece?,
        process: (move: MoveMetadata, previousCapture: PieceMetadata?) -> Continuation
    ) {
        directions.forEach { moveInDirection(it, piece, getPiece, fromSquare, fromSquare, null, process) }
    }

    private fun moveInDirection(
        direction: T,
        piece: Piece,
        getPiece: (Square) -> Piece?,
        fromSquare: Square,
        previousSquare: Square,
        previousCapture: PieceMetadata?,
        process: (move: MoveMetadata, previousCapture: PieceMetadata?) -> Continuation
    ) {
        val toSquare = previousSquare.transform(direction)
        if(toSquare != null) {
            val occupant = getPiece(toSquare)
            val capture = if (occupant?.color == piece.color.opposite) PieceMetadata(occupant, toSquare) else null
            val support = if (occupant?.color == piece.color) PieceMetadata(occupant, toSquare) else null
            val moveMeta = MoveMetadata(Move.Basic(fromSquare, toSquare), piece, capturing = capture, supporting = support)
            if (previousCapture == null || capture != null) {
                val then = process(moveMeta, previousCapture)
                if (then == Continuation.KeepGoing) {
                    moveInDirection(direction, piece, getPiece, fromSquare, toSquare, capture, process)
                }
            } else moveInDirection(direction, piece, getPiece, fromSquare, toSquare, previousCapture, process)
        }
    }
}

object RookAndQueenMoveCalculator: LineMoveCalculatorIn<Direction.HorizontalAndVertical> {
    override val directions = Direction.HorizontalAndVertical.values()
    override fun Square.transform(direction: Direction.HorizontalAndVertical): Square? = when (direction) {
        U -> this.transform(0, 1)
        D -> this.transform(0, -1)
        L -> this.transform(-1, 0)
        R -> this.transform(1, 0)
    }
}

object BishopAndQueenMoveCalculator: LineMoveCalculatorIn<Direction.Diagonal> {
    override val directions = Direction.Diagonal.values()
    override fun Square.transform(direction: Direction.Diagonal): Square? = when (direction) {
        UL -> this.transform(-1, 1)
        UR -> this.transform(1, 1)
        DL -> this.transform(-1, -1)
        DR -> this.transform(1, -1)
    }
}

