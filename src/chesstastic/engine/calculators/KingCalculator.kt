package chesstastic.engine.calculators

import chesstastic.engine.entities.*
import chesstastic.engine.entities.Rank.*
import chesstastic.engine.entities.File.*
import chesstastic.engine.entities.Color.*
import chesstastic.engine.entities.metadata.CastleMetadata
import chesstastic.engine.entities.metadata.HistoryMetadata

object KingCalculator: MoveCalculator, AttackCalculator {
    override fun attackers(target: Square, attacker: Color, board: Board): List<Pair<Piece,Square>> {
        val attackersKing = board.kingSquare(attacker)
        return adjacentSquares(target)
            .asSequence()
            .filter { it == attackersKing }
            .map { Pair(Piece(PieceKind.King, attacker), it)}
            .toList()
    }

    override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> {
        val regularMoves = adjacentSquares(fromSquare)
            .asSequence()
            .filterNot { board.isOccupiedByColor(it, color) }
            .map { Move.Basic(fromSquare, it) }
            .toList()

        return regularMoves + castleMoves(color, board)
    }

    private fun castleMoves(color: Color, board: Board): List<Move.Castle> {
        val info = CastlingInfo(color, board.historyMetadata)
        return if (!info.metadata.kingHasMoved && !board.isInCheck(color)) {
            listOfNotNull(
                tryCastle(board, info, Move.Castle.Kingside(color)),
                tryCastle(board, info, Move.Castle.Queenside(color))
            )
        } else listOf()
    }

    private fun tryCastle(board: Board, info: CastlingInfo, move: Move.Castle): Move.Castle? =
        when (move) {
            is Move.Castle.Kingside -> when {
                info.metadata.kingsideRookHasMoved -> null
                info.kingsidePassingSquares.any { board[it] != null } -> null
                info.kingsidePassingSquares.any {
                    board.isSquareAttacked(it, attacker = info.color.opposite)
                } -> null
                else -> move
            }
            is Move.Castle.Queenside -> when {
                info.metadata.queensideRookHasMoved -> null
                info.queensidePassingSquares.any { board[it] != null } -> null
                info.queensidePassingSquares.any {
                    board.isSquareAttacked(it, attacker = info.color.opposite)
                } -> null
                else -> move
            }
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


    private class CastlingInfo(val color: Color, history: HistoryMetadata) {
        val metadata: CastleMetadata = when (color) {
            Color.Light -> history.lightCastleMetadata
            Color.Dark -> history.darkCastleMetadata
        }
        val rank = if (color == Light) _1 else _8
        val kingsidePassingSquares by lazy { listOf(Square(F, rank), Square(G, rank)) }
        val queensidePassingSquares by lazy { listOf(Square(D, rank), Square(C, rank)) }
    }
}

