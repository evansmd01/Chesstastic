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
        override fun fromIndex(index: Int): Rank? =
                Rank.values().firstOrNull { it.index == index }
    }
}
