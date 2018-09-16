package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*
import chesstastic.engine.rules.MoveCalculator

class PawnMoveCalculator(val piece: Pawn, val currentCoord: Square, val board: Board): PieceMoveCalculator {

    private val rankDelta = if (piece.color == Color.Light) 1 else -1
    private val opponent = piece.color.opposite
    private val enPassantRank: Rank = if (piece.color == Color.Light) Rank._5 else Rank._4
    private val promotionRank: Rank = if (piece.color == Color.Light) Rank._8 else Rank._1

    override val attackingSquares: Iterable<Square> by lazy {
        listOfNotNull(currentCoord.transform(1, rankDelta), currentCoord.transform(-1, rankDelta))
    }

    override val legalMoves: Iterable<Move> by lazy {
        potentialMoves.filterNot { move ->
            MoveCalculator.isKingInCheck(piece.color, board.updated(move))
        }
    }

    /**
     * Moves that have not been validated for legality, but which obey the movement abilities of the piece
     */
    private val potentialMoves: Iterable<Move> by lazy {
        val forwardMoves = mutableListOf<Move>()

        // try to move forward
        val forwardOne = currentCoord.transform(0, rankDelta)!!
        var isBlocked = board[forwardOne] != null
        if (!isBlocked) {
            // promotion
            if(forwardOne.rank == promotionRank) {
                forwardMoves.add(Move.Promotion(currentCoord, forwardOne, Queen(piece.color)))
                forwardMoves.add(Move.Promotion(currentCoord, forwardOne, Knight(piece.color)))
            } else
                forwardMoves.add(Move.Basic(currentCoord, forwardOne))

            // Double starting move
            if (currentCoord.rank == startingRank(piece.color)) {
                val forwardTwo = forwardOne.transform(0, rankDelta)!!
                isBlocked = board[forwardTwo] != null
                if (!isBlocked) forwardMoves.add(Move.Basic(currentCoord, forwardTwo))
            }
        }

        // try to capture diagonals
        val captureMoves = attackingSquares.flatMap { target ->
            val isOccupiedByOpponentPiece = board[target]?.color == opponent
            val isPromotable = target.rank == promotionRank
            when {
                isOccupiedByOpponentPiece && isPromotable -> listOf(
                    Move.Promotion(currentCoord, target, Queen(piece.color)),
                    Move.Promotion(currentCoord, target, Knight(piece.color))
                )
                isOccupiedByOpponentPiece -> listOf(Move.Basic(currentCoord, target))
                isEnPassantEligible(target) -> listOf(Move.EnPassant(currentCoord, target))
                else -> listOf()
            }
        }

        forwardMoves + captureMoves
    }

    private fun isEnPassantEligible(target: Square): Boolean {
        if (currentCoord.rank != enPassantRank) return false
        return board.history.last() == enPassantOpening(target.file)
    }

    private fun enPassantOpening(file: File): Move = Move.Basic(
            from = Square(file, startingRank(opponent)),
            to = Square(file, enPassantRank))

    companion object {
        private fun startingRank(color: Color) = if (color == Color.Light) Rank._2 else Rank._7
    }
}

