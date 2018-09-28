package chesstastic.util

class ConsoleColor {
    companion object {
        const val RESET = "\u001B[0m"
        const val BLACK = "\u001B[30m"
        const val RED = "\u001B[31m"
        const val GREEN = "\u001B[32m"
        const val YELLOW = "\u001B[33m"
        const val PURPLE = "\u001B[35m"
        const val CYAN = "\u001B[36m"
        const val WHITE = "\u001B[37m"
    }
}

private fun printlnColor(colorEncoding: String, message: String) = println("$colorEncoding$message${ConsoleColor.RESET}")

fun printlnRed(message: String) = printlnColor(ConsoleColor.RED, message)

fun printlnGreen(message: String) = printlnColor(ConsoleColor.GREEN, message)

fun printlnYellow(message: String) = printlnColor(ConsoleColor.YELLOW, message)

fun printlnCyan(message: String) = printlnColor(ConsoleColor.CYAN, message)
