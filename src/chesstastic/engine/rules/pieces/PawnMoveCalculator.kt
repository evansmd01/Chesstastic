package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*
import chesstastic.engine.rules.MoveCalculator

class PawnMoveCalculator(val piece: Pawn, val currentCoord: Coordinate, val board: Board): PieceMoveCalculator {

    private val rankDelta = if (piece.color == Color.Light) 1 else -1
    private val opponent = piece.color.opposite
    private val enPassantRank: Rank = if (piece.color == Color.Light) Rank.Five else Rank.Four
    private val promotionRank: Rank = if (piece.color == Color.Light) Rank.Eight else Rank.One

    override val coordinatesUnderAttack: Iterable<Coordinate> by lazy {
        listOfNotNull(currentCoord.transform(1, rankDelta), currentCoord.transform(-1, rankDelta))
    }

    override val legalMoves: Iterable<Move> by lazy {
        potentialMoves.filterNot { move ->
            MoveCalculator.isKingInCheck(piece.color, board.update(move))
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
                forwardMoves.add(PawnPromotionMove(currentCoord, forwardOne, Queen(piece.color)))
                forwardMoves.add(PawnPromotionMove(currentCoord, forwardOne, Knight(piece.color)))
            } else
                forwardMoves.add(BasicMove(currentCoord, forwardOne))

            // Double starting move
            if (currentCoord.rank == startingRank(piece.color)) {
                val forwardTwo = forwardOne.transform(0, rankDelta)!!
                isBlocked = board[forwardTwo] != null
                if (!isBlocked) forwardMoves.add(BasicMove(currentCoord, forwardTwo))
            }
        }

        // try to capture diagonals
        val captureMoves = coordinatesUnderAttack.mapNotNull { target ->
            val isOccupiedByOpponentPiece = board[target]?.color == opponent
            when {
                isOccupiedByOpponentPiece -> BasicMove(currentCoord, target)
                isEnPassantEligible(target) -> EnPassantMove(currentCoord, target, board.history.last().to)
                else -> null
            }
        }

        forwardMoves + captureMoves
    }

    private fun isEnPassantEligible(target: Coordinate): Boolean {
        if (currentCoord.rank != enPassantRank) return false
        return board.history.last() == enPassantOpening(target.file)
    }

    private fun enPassantOpening(file: File): Move = BasicMove(
            from = Coordinate(file, startingRank(opponent)),
            to = Coordinate(file, enPassantRank))

    companion object {
        private fun startingRank(color: Color) = if (color == Color.Light) Rank.Two else Rank.Seven
    }
}

