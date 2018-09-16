package chesstastic.cli.commands

import chesstastic.engine.entities.*

interface CommandParser {
    fun parse(input: String): Command?
}

sealed class Command {
    companion object {
        private val parsers = listOf(
                Exit,
                Move,
                Print,
                Test
        )

        fun parse(input: String): Command? = parsers
                .map { it.parse(input) }
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

    class Move(val from: Coordinate, val to: Coordinate): Command() {
        companion object: CommandParser {
            private val regex =
                    """^\s*([a-hA-H][1-8])\s*([a-hA-H][1-8])\s*$"""
                            .toRegex()

            override fun parse(input: String): Move? {
                val match = regex.matchEntire(input)
                if (match != null) {
                    val (fromInput, toInput) = match.destructured
                    val maybeFrom = Coordinate.parse(fromInput)
                    val maybeTo = Coordinate.parse(toInput)
                    if(maybeFrom != null && maybeTo != null) {
                        return Move(maybeFrom, maybeTo)
                    }
                }
                return null
            }
        }
    }

    class Print : Command() {
        companion object: CommandParser {
            override fun parse(input: String): Command? {
                return if(input.toLowerCase().trim() == "print")
                    Print()
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
}


