package chesstastic.engine.calculators

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.File.*
import chesstastic.engine.entities.Color.*
import kotlin.coroutines.experimental.buildSequence

object KingCalculator: MoveCalculator, AttackCalculator {
    override fun attackers(target: Square, attacker: Color, board: Board): List<Pair<Piece,Square>> {
        val attackersKing = board.kingSquare(attacker)
        return adjacentSquares(target)
            .filter { it == attackersKing }
            .map { Pair(Piece(PieceKind.King, attacker), it)}
    }

    override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> {
        val regularMoves = adjacentSquares(fromSquare)
            .filterNot { board.isOccupiedByColor(it, color) }
            .map { Move.Basic(fromSquare, it) }

        return regularMoves + castleMoves(color, fromSquare, board)
    }

    private fun castleMoves(color: Color, fromSquare: Square, board: Board): Sequence<Move.Castle> {
        val info = CastlingInfo(color)
        return buildSequence {
            if (fromSquare == info.kingStart && !board.isInCheck(color) && board.history.none { it.from == info.kingStart }) {
                tryCastle(board, color, info.kingsideRook, info.kingsidePassingSquares, { Move.KingsideCastle(color) })?.let { yield(it) }
                tryCastle(board, color, info.queensideRook, info.queensidePassingSquares, { Move.QueensideCastle(color) })?.let { yield(it) }
            }
        }
    }

    private fun tryCastle(board: Board, color: Color, rookSquare: Square, passingSquares: Iterable<Square>, move: () -> Move.Castle): Move.Castle? =
        when {
            // is blocked by any pieces
            passingSquares.any { board[it] != null } -> null
            // rook has moved
            board.history.any { it.from == rookSquare } -> null
            // is passing through check
            passingSquares.any { board.isSquareAttacked(it, attacker = color.opposite) } -> null
            // else it's legal to castle!
            else -> move()
        }

    private fun adjacentSquares(fromSquare: Square): List<Square> =
        listOfNotNull(
            fromSquare.transform(1, 0),
            fromSquare.transform(1, 1),
            fromSquare.transform(0, 1),
            fromSquare.transform(-1, 0),
            fromSquare.transform(-1, -1),
            fromSquare.transform(0, -1),
            fromSquare.transform(1, -1),
            fromSquare.transform(-1, 1)
        )


    private class CastlingInfo(color: Color) {
        val rank = if (color == Light) _1 else _8
        val kingStart = Square(E, rank)
        val kingsideRook by lazy { Square(H, rank) }
        val queensideRook by lazy { Square(A, rank) }
        val kingsidePassingSquares by lazy { listOf(Square(F, rank), Square(G, rank)) }
        val queensidePassingSquares by lazy { listOf(Square(D, rank), Square(C, rank)) }
    }
}

