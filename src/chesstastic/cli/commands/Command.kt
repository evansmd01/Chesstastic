package chesstastic.cli.commands

import chesstastic.engine.entities.*

interface CommandParser {
    fun parse(input: String): Command?
}

sealed class Command {
    companion object {
        private val parsers = listOf(
            Exit,
            Export,
            Test,
            Load,
            ShowMoves,
            Move
        )

        fun parse(input: String): Command? = parsers
                .map { it.parse(input.trim()) }
                .firstOrNull { it != null }
    }

    class Exit: Command() {
        companion object: CommandParser {
            override fun parse(input: String): Exit? =
                    if (input.toLowerCase() == "exit")
                        Exit()
                    else null
        }
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

    class Test : Command() {
        companion object: CommandParser {
            override fun parse(input: String): Command? {
                return if(input.toLowerCase().trim() == "test")
                    Test()
                else null
            }

        }
    }

    class Load(val history: String): Command() {
        companion object: CommandParser {
            override fun parse(input: String): Command? {
                return if(input.startsWith("load ")) {
                    Load(history = input.substring(4).trim())
                } else null
            }

        }
    }

    class ShowMoves: Command() {
        companion object: CommandParser {
            override fun parse(input: String): Command? {
                return if(input.toLowerCase() == "show potentialMoves") {
                    ShowMoves()
                } else null
            }

        }
    }
}


