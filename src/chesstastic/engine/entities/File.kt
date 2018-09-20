package chesstastic.engine.entities

enum class File {
    A, B, C, D, E, F, G, H;

    val index: Int
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

    companion object {
        fun fromIndex(index: Int): File? =
            File.values().firstOrNull { it.index == index }
    }
}
