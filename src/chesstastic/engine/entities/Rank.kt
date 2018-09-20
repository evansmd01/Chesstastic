package chesstastic.engine.entities

enum class Rank {
    _1,
    _2,
    _3,
    _4,
    _5,
    _6,
    _7,
    _8;

    val index: Int
        get() = when(this) {
            _1 -> 0
            _2 -> 1
            _3 -> 2
            _4 -> 3
            _5 -> 4
            _6 -> 5
            _7 -> 6
            _8 -> 7
        }

    override fun toString(): String = (index + 1).toString()

    companion object {
        fun fromIndex(index: Int): Rank? =
            Rank.values().firstOrNull { it.index == index }
    }
}
