package chesstastic.util

import chesstastic.engine.entities.Color

class ConsoleColor {
    companion object {
        const val RESET = "\u001B[0m"
        const val RED = "\u001B[31m"
        const val GREEN = "\u001B[32m"
        const val YELLOW = "\u001B[33m"
        const val PURPLE = "\u001B[35m"
        const val CYAN = "\u001B[36m"

        val all = setOf(
            RESET, RED, GREEN, YELLOW, PURPLE, CYAN
        )
    }
}

private fun applyConsoleColor(colorEncoding: String, message: String) = "$colorEncoding$message${ConsoleColor.RESET}"

fun printlnRed(message: String) = println(applyConsoleColor(ConsoleColor.RED, message))

fun printlnGreen(message: String) = println(applyConsoleColor(ConsoleColor.GREEN, message))

fun printlnYellow(message: String) = println(applyConsoleColor(ConsoleColor.YELLOW, message))

fun printlnCyan(message: String) = println(applyConsoleColor(ConsoleColor.CYAN, message))

fun String.applyColor(color: Color): String = applyConsoleColor(when (color) {
    Color.Light -> ConsoleColor.CYAN
    Color.Dark -> ConsoleColor.PURPLE
}, this)
