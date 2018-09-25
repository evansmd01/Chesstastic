package chesstastic.cli.commands

import chesstastic.engine.entities.Move

interface CommandParser {
    fun parse(input: String): Command?
}

sealed class Command {
    companion object {
        private val parsers = listOf(
            Import,
            Export,
            MoveCommand,
            SetAi,
            SetStockfish,
            RunTask
        )

        fun parse(input: String): Command? = parsers
            .asSequence()
            .map { it.parse(input.trim()) }
            .firstOrNull { it != null }
    }

    class MoveCommand(val move: Move): Command() {
        companion object: CommandParser {
            override fun parse(input: String): MoveCommand? {
                return chesstastic.engine.entities.Move.parse(input)?.let { MoveCommand(it) }
            }
        }
    }

    class Export : Command() {
        companion object: CommandParser {
            override fun parse(input: String): Command? {
                return if(input.toLowerCase().trim() == "export")
                    Export()
                else null
            }
        }
    }

    class Import(val history: String): Command() {
        companion object: CommandParser {
            override fun parse(input: String): Command? {
                return if(input.startsWith("import ")) {
                    Import(history = input.substring(4).trim())
                } else null
            }

        }
    }

    data class SetStockfish(val moveTimeMillis: Long): Command() {
        companion object: CommandParser {
            private val regex = """set\s+stockfish\s+(\d+)""".toRegex()
            override fun parse(input: String): SetStockfish? {
                val match = regex.matchEntire(input.toLowerCase())
                return if (match != null) {
                    val (moveTime) = match.destructured
                    SetStockfish(moveTime.toLong())
                } else null
            }
        }
    }

    data class SetAi(val breadth: Int = 0, val depth: Int = 0): Command() {
        companion object : CommandParser {
            private val regex = """set\s+ai\s+(\d+)\s+(\d+)""".toRegex()
            override fun parse(input: String): SetAi? {
                val match = regex.matchEntire(input.toLowerCase())
                return if (match != null) {
                    val (depth, breadth) = match.destructured
                    SetAi(depth.toInt(), breadth.toInt())
                } else null
            }
        }
    }

    data class RunTask(val taskName: String): Command() {
        companion object: CommandParser {
            override fun parse(input: String): RunTask? {
                return if(input.toLowerCase().startsWith("run ")) {
                    RunTask(input.substringAfter("run ").trim())
                } else null
            }
        }
    }
}


