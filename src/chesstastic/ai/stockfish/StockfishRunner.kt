package chesstastic.ai.stockfish

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * A simple and efficient client to run Stockfish from Java
 *
 * @author Rahul A R
 */
class StockfishRunner {

    private var engineProcess: Process? = null
    private var processReader: BufferedReader? = null
    private var processWriter: OutputStreamWriter? = null

    /**
     * Starts Stockfish engine as a process and initializes it
     *
     * @param None
     * @return True on success. False otherwise
     */
    fun startEngine() {
        engineProcess = Runtime.getRuntime().exec(PATH)
        processReader = BufferedReader(InputStreamReader(
            engineProcess!!.inputStream))
        processWriter = OutputStreamWriter(
            engineProcess!!.outputStream)
    }

    /**
     * Takes in any valid UCI command and executes it
     *
     * @param command
     */
    fun sendCommand(command: String) {
        try {
            processWriter!!.write(command + "\n")
            processWriter!!.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * This is generally called right after 'sendCommand' for getting the raw
     * output from Stockfish
     *
     * @param waitTime
     * Time in milliseconds for which the function waits before
     * reading the output. Useful when a long running command is
     * executed
     * @return Raw output from Stockfish
     */
    fun getOutput(waitTime: Int): String {
        val buffer = StringBuffer()
        try {
            Thread.sleep(waitTime.toLong())
            sendCommand("isready")
            while (true) {
                val text = processReader!!.readLine()
                if (text == "readyok")
                    break
                else
                    buffer.append(text + "\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return buffer.toString()
    }

    /**
     * This function returns the best move for a given position after
     * calculating for 'waitTime' ms
     *
     * @param moves
     * Position string
     * @param waitTime
     * in milliseconds
     * @return Best Move in PGN format
     */
    fun getBestMove(moves: String, waitTime: Int): String? {
        sendCommand("position startpos moves $moves")
        sendCommand("go movetime $waitTime")
        val bestMoveLine = getOutput(waitTime + 500).lines()
            .filter { it.trim().isNotEmpty() }
            .last()

        val match = Regex("""bestmove\s([a-h][1-8][a-h][1-8][qk]?)""").find(bestMoveLine)
        return match?.groupValues?.elementAt(1)
    }

    /**
     * Stops Stockfish and cleans up before closing it
     */
    fun stopEngine() {
        try {
            sendCommand("quit")
            processReader!!.close()
            processWriter!!.close()
        } catch (e: IOException) {
        }
    }

    /**
     * Get the evaluation score of a given board position
     * @param fen Position string
     * @param waitTime in milliseconds
     * @return evalScore
     */
    fun getEvalScore(fen: String, waitTime: Int): Float {
        sendCommand("position fen $fen")
        sendCommand("go movetime $waitTime")

        var evalScore = 0.0f
        val dump = getOutput(waitTime + 20).split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in dump.indices.reversed()) {
            if (dump[i].startsWith("info depth ")) {
                try {
                    evalScore = java.lang.Float.parseFloat(dump[i].split("score cp ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                        .split(" nodes".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                } catch (e: Exception) {
                    evalScore = java.lang.Float.parseFloat(dump[i].split("score cp ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                        .split(" upperbound nodes".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                }

            }
        }
        return evalScore / 100
    }

    companion object {
        val PATH: String by lazy {
            "${System.getProperty("user.dir")}/lib/Stockfish/src/stockfish"
        }
    }
}
