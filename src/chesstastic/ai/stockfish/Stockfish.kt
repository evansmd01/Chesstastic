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
        val process = StockfishProcess.start()
        try {
            val history = board.history.joinToString(separator = " ").toLowerCase()
            val move = process.getBestMove(history, timeLimit.toMillis().toInt())
                ?: throw Exception("Stockfish could not select a move")
            return Move.parse(move) ?: throw Exception("Could not parse move: '$move'")
        } finally {
            process.close()
        }
    }
}

/**
 * Based on CLI Instructions http://support.stockfishchess.org/kb/advanced-topics/uci-protocol
 */
private class StockfishProcess(val process: Process, val reader: BufferedReader, val writer: OutputStreamWriter) {
    fun sendCommand(command: String) {
        writer.write(command + "\n")
        writer.flush()
    }

    fun getOutput(waitTime: Int): String {
        val buffer = StringBuffer()
        Thread.sleep(waitTime.toLong())
        sendCommand("isready")
        while (true) {
            val text = reader.readLine()
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

    fun close() {
        sendCommand("quit")
        reader.close()
        writer.close()
        process.destroyForcibly()
    }

    companion object {
        val EXECUTABLE = "${System.getProperty("user.dir")}/lib/Stockfish/src/stockfish"

        fun start(): StockfishProcess {
            if (!File(EXECUTABLE).exists())
                throw Exception("Stockfish has not been installed. Follow the instructions in the readme.")
            val process = Runtime.getRuntime().exec(EXECUTABLE)
            return StockfishProcess(
                process,
                BufferedReader(InputStreamReader(process.inputStream)),
                OutputStreamWriter(process.outputStream)
            )
        }
    }
}
