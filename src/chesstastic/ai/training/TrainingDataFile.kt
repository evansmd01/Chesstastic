package chesstastic.ai.training

import chesstastic.engine.entities.Board
import java.io.File

class TrainingDataFile(val file: File) {
    fun writeAll(boards: Sequence<Board>) {
        boards.forEach {
            file.appendText("${it.historyMetadata.history}\n")
        }
    }

    fun readAll(): Sequence<Board> =
        file.reader().buffered().lineSequence().map { Board.parseHistory(it) }
}
