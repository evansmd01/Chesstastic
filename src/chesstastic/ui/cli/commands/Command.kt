package chesstastic.ui.cli.commands

import chesstastic.engine.Coordinate

interface CommandParser {
    fun parse(input: String): Command?
}

sealed class Command {
    companion object {
        private val parsers = listOf(
                Exit.Companion,
                Move.Companion
        )

        fun parse(input: String): Command? = parsers
                .map { it.parse(input) }
                .firstOrNull { it != null }
    }
}

class Exit(): Command() {
    companion object: CommandParser {
        override fun parse(input: String): Exit? =
            if (input.toLowerCase() == "exit")
                Exit()
            else null
    }
}

data class Move(val from: Coordinate, val to: Coordinate): Command() {
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
