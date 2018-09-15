package chesstastic.engine.rules

import chesstastic.engine.entities.*

class MoveCalculator(val board: Board) {
    val validMoves: Iterable<Move> get() {
        return File.values().flatMap { file ->
            Rank.values().flatMap { rank ->
                val piece = board[file, rank]
                if (piece?.color == board.turn) {
                    moveAnyDamnPlace(from = Coordinate(file, rank))
                } else listOf()
            }
        }
    }


    private fun moveAnyDamnPlace(from: Coordinate): List<Move> =
            File.values().flatMap { file ->
                Rank.values().map { rank ->
                    Move(from, to = Coordinate(file, rank))
                }
            }
}
