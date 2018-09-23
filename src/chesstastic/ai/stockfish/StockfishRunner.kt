package chesstastic.ai.stockfish

import java.io.*

/**
 * Based on CLI Instructions http://support.stockfishchess.org/kb/advanced-topics/uci-protocol
 */
class StockfishRunner {
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
