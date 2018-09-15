package chesstastic.engine.entities

interface Indexable {
    val index: Int
}

interface IndexableCompanion<T: Indexable> {
    fun fromIndex(index: Int): T
}
