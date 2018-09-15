package chesstastic.cli.commands

import chesstastic.engine.entities.Coordinate

interface CommandParser {
    fun parse(input: String): Command?
}

sealed class Command {
    companion object {
        private val parsers = listOf(
                ExitCommand,
                MoveCommand
        )

        fun parse(input: String): Command? = parsers
                .map { it.parse(input) }
                .firstOrNull { it != null }
    }
}

class ExitCommand(): Command() {
    companion object: CommandParser {
        override fun parse(input: String): ExitCommand? =
            if (input.toLowerCase() == "exit")
                ExitCommand()
            else null
    }
}

data class MoveCommand(val from: Coordinate, val to: Coordinate): Command() {
    companion object: CommandParser {
        private val regex =
                """^\s*([a-hA-H][1-8])\s*([a-hA-H][1-8])\s*$"""
                .toRegex()

        override fun parse(input: String): MoveCommand? {
            val match = regex.matchEntire(input)
            if (match != null) {
                val (fromInput, toInput) = match.destructured
                val maybeFrom = Coordinate.parse(fromInput)
                val maybeTo = Coordinate.parse(toInput)
                if(maybeFrom != null && maybeTo != null) {
                    return MoveCommand(maybeFrom, maybeTo)
                }
            }
            return null
        }
    }
}
