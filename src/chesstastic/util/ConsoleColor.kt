package chesstastic.util

class ConsoleColor {
    companion object {
        val RESET = "\u001B[0m"
        val BLACK = "\u001B[30m"
        val RED = "\u001B[31m"
        val GREEN = "\u001B[32m"
        val YELLOW = "\u001B[33m"
        val BLUE = "\u001B[34m"
        val PURPLE = "\u001B[35m"
        val CYAN = "\u001B[36m"
        val WHITE = "\u001B[37m"
    }
}

fun printlnRed(message: String) = printlnColor(ConsoleColor.RED, message)

fun printlnColor(colorEncoding: String, message: String) = println("$colorEncoding$message${ConsoleColor.RESET}")

fun printlnGreen(message: String) = printlnColor(ConsoleColor.GREEN, message)
