package chesstastic.ai.stockfish

import chesstastic.ai.AIPlayer
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Move
import java.lang.Exception
import java.time.Duration


class Stockfish(private val timeLimit: Duration): AIPlayer {
    override fun selectMove(board: Board): Move {
        val runner = StockfishRunner()
        try {
            runner.startEngine()
            val history = board.history.joinToString(separator = " ").toLowerCase()
            val move = runner.getBestMove(history, timeLimit.toMillis().toInt()) ?:
                throw Exception("Stockfish could not select a move")
            return Move.parse(move) ?:
                throw Exception("Could not parse move: '$move'")
        } finally {
            runner.stopEngine()
        }
    }
}
