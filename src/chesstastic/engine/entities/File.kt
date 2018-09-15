package chesstastic.engine.entities

enum class File: Indexable {
    A, B, C, D, E, F, G, H;

    override val index: Int
        get() = when(this) {
            A -> 0
            B -> 1
            C -> 2
            D -> 3
            E -> 4
            F -> 5
            G -> 6
            H -> 7
        }

    companion object: IndexableCompanion<File> {
        override fun fromIndex(index: Int): File? =
            File.values().firstOrNull { it.index == index }
    }
}
