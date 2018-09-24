package chesstastic.engine.position.calculators

import chesstastic.engine.entities.*

interface StraightLineCalculator<T> {
    val matches: (PieceKind) -> Boolean
    fun Square.transform(direction: T): Square?
    fun directions(): Array<T>

    fun movesHeading(
        direction: T,
        color: Color,
        fromSquare: Square,
        board: Board): Iterable<Square> {
        val nextSquare = fromSquare.transform(direction)
        val occupant = nextSquare?.let { board[it] }
        return if(nextSquare != null && occupant?.color != color) {
            if(occupant != null) {
                // end recursion, we've found a piece of the opposite player.
                listOf(nextSquare)
            } else {
                // empty square, continue recursively finding moves
                movesHeading(direction, color, nextSquare, board) + nextSquare
            }
        } else listOf() // end recursion, not including this friendly square
    }
}

interface StraightLineAttackCalculator<T>: StraightLineCalculator<T>, AttackCalculator {
    override fun attackers(target: Square, attacker: Color, board: Board): List<Pair<Piece, Square>> {
        // The recursion return the squares in reverse order,
        // which is handy since only the last move in any direction could be a piece
        val friendlyColor = attacker.opposite
        return directions().mapNotNull {
            movesHeading(it, friendlyColor, target, board).firstOrNull()
        }.mapNotNull { square ->
            val piece = board[square]
            if(piece != null && matches(piece.kind))
                piece to square
            else null
        }
    }
}

interface StraightLineMoveCalculator<T>: StraightLineCalculator<T>, MoveCalculator {
    override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> =
        directions()
            .flatMap { movesHeading(it, color, fromSquare, board) }
            .map { Move.Basic(fromSquare, it) }
}

class HorizontalCalculator(override val matches: (PieceKind) -> Boolean): StraightLineAttackCalculator<HorizontalDirection>, StraightLineMoveCalculator<HorizontalDirection> {
    override fun directions() = HorizontalDirection.values()
    override fun Square.transform(direction: HorizontalDirection): Square? = when (direction) {
        HorizontalDirection.U -> this.transform(0, 1)
        HorizontalDirection.D -> this.transform(0, -1)
        HorizontalDirection.L -> this.transform(-1, 0)
        HorizontalDirection.R -> this.transform(1, 0)
    }
}

class DiagonalCalculator(override val matches: (PieceKind) -> Boolean): StraightLineAttackCalculator<DiagonalDirection>, StraightLineMoveCalculator<DiagonalDirection> {
    override fun directions() = DiagonalDirection.values()
    override fun Square.transform(direction: DiagonalDirection) = when (direction) {
        DiagonalDirection.UL -> this.transform(-1, 1)
        DiagonalDirection.UR -> this.transform(1, 1)
        DiagonalDirection.DL -> this.transform(-1, -1)
        DiagonalDirection.DR -> this.transform(1, -1)
    }
}

enum class HorizontalDirection {
    U,D,L,R
}

enum class DiagonalDirection {
    UL, UR, DL, DR
}

