package chesstastic.ai.stockfish

import chesstastic.ai.AIPlayer
import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Move
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
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

/**
 * Based on CLI Instructions http://support.stockfishchess.org/kb/advanced-topics/uci-protocol
 */
private class StockfishRunner {
    private var engineProcess: Process? = null
    private var processReader: BufferedReader? = null
    private var processWriter: OutputStreamWriter? = null

    fun startEngine() {
        if (!File(EXECUTABLE).exists())
            throw Exception("Stockfish has not been installed. Follow the instructions in the readme.")

        engineProcess = Runtime.getRuntime().exec(EXECUTABLE)
        processReader = BufferedReader(InputStreamReader(
            engineProcess!!.inputStream))
        processWriter = OutputStreamWriter(
            engineProcess!!.outputStream)
    }

    fun sendCommand(command: String) {
        processWriter?.write(command + "\n")
        processWriter?.flush()
    }

    fun getOutput(waitTime: Int): String {
        val buffer = StringBuffer()
        Thread.sleep(waitTime.toLong())
        sendCommand("isready")
        while (true) {
            val text = processReader?.readLine()
            if (text == "readyok")
                break
            else
                buffer.append(text + "\n")
        }
        return buffer.toString()
    }

    fun getBestMove(moves: String, waitTime: Int): String? {
        sendCommand("position startpos moves $moves")
        sendCommand("go movetime $waitTime")
        val bestMoveLine = getOutput(waitTime + 500).lines().last { it.trim().isNotEmpty() }
        val match = Regex("""bestmove\s([a-h][1-8][a-h][1-8][qk]?)""").find(bestMoveLine)
        return match?.groupValues?.elementAt(1)
    }

    fun stopEngine() {
        sendCommand("quit")
        processReader?.close()
        processWriter?.close()
    }

    companion object {
        val EXECUTABLE = "${System.getProperty("user.dir")}/lib/Stockfish/src/stockfish"
    }
}
