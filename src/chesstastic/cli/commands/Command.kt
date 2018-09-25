package chesstastic.cli.commands

import chesstastic.engine.entities.*

interface CommandParser {
    fun parse(input: String): Command?
}

sealed class Command {
    companion object {
        private val parsers = listOf(
            DisableMoveValidation,
            Import,
            Export,
            Move,
            SetAi,
            SetStockfish,
            RunTask
        )

        fun parse(input: String): Command? = parsers
            .asSequence()
            .map { it.parse(input.trim()) }
            .firstOrNull { it != null }
    }

    class Move(val from: Square, val to: Square): Command() {
        companion object: CommandParser {
            private val regex =
                    """^\s*([a-hA-H][1-8])\s*([a-hA-H][1-8])\s*$"""
                            .toRegex()

            override fun parse(input: String): Move? {
                val match = regex.matchEntire(input)
                if (match != null) {
                    val (fromInput, toInput) = match.destructured
                    val maybeFrom = Square.parse(fromInput)
                    val maybeTo = Square.parse(toInput)
                    if(maybeFrom != null && maybeTo != null) {
                        return Move(maybeFrom, maybeTo)
                    }
                }
                return null
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

    class DisableMoveValidation: Command() {
        companion object: CommandParser {
            override fun parse(input: String): Command? {
                return if(input.toLowerCase() == "disable validation") {
                    return DisableMoveValidation()
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


