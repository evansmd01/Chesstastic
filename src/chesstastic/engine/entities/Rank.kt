package chesstastic.engine.entities

enum class Rank: Indexable {
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight;

    override val index: Int
        get() = when(this) {
            One -> 0
            Two -> 1
            Three -> 2
            Four -> 3
            Five -> 4
            Six -> 5
            Seven -> 6
            Eight -> 7
        }

    override fun toString(): String = (index + 1).toString()

    companion object: IndexableCompanion<Rank> {
        override fun fromIndex(index: Int): Rank {
            val maybeRank = Rank.values().find { it.index == index }
            return maybeRank ?: throw IndexOutOfBoundsException("$index is an invalid index for Rank")
        }

        fun valueOf(number: Int): Rank = when(number) {
            1 -> One
            2 -> Two
            3 -> Three
            4 -> Four
            5 -> Five
            6 -> Six
            7 -> Seven
            8 -> Eight
            else -> throw IndexOutOfBoundsException("$number is not a valid rank")
        }
    }
}
