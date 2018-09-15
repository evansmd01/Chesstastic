package chesstastic.engine.entities

enum class Color {
    Dark, Light;

    val opposite: Color get() {
        return when (this) {
            Light -> Dark
            Dark -> Light
        }
    }
}
