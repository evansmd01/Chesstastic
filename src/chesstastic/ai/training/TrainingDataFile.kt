package chesstastic.ai.training

import chesstastic.engine.entities.Board
import chesstastic.engine.entities.Move
import java.io.File

class TrainingDataFile(filename: String) {
    private val file = File("${System.getProperty("user.dir")}/data/training/$filename")
    fun writeAll(boards: Sequence<Board>) {
        file.parentFile.mkdirs()
        boards.forEach {
            file.appendText("${it.historyMetadata.history}\n")
        }
    }

    fun readAllHistory(): Sequence<List<Move>> =
        file.reader().buffered().lineSequence().map { line ->
            line.trim().split(" ").map { Move.parse(it) ?: throw Exception("Could not parse move $it") }
        }
}
