package chesstastic.engine.rules.pieces

import chesstastic.engine.entities.*

class QueenMoveCalculator {
    companion object: PieceMoveCalculator {
        private val horizontalCalculator = object: HorizontalMoveCalculator {
            override fun isCorrectPiece(piece: Piece): Boolean = piece is Queen
        }
        private val diagonalCalculator = object: DiagonalMoveCalculator {
            override fun isCorrectPiece(piece: Piece): Boolean = piece is Queen
        }

        override fun timesSquareIsAttacked(target: Square, attacker: Color, board: Board): Int =
            horizontalCalculator.timesSquareIsAttacked(target, attacker, board) +
                diagonalCalculator.timesSquareIsAttacked(target, attacker, board)

        override fun potentialMoves(color: Color, fromSquare: Square, board: Board): Iterable<Move> =
            horizontalCalculator.potentialMoves(color, fromSquare, board) +
                diagonalCalculator.potentialMoves(color, fromSquare, board)
    }
}

