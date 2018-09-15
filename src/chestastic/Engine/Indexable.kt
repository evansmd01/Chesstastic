package chestastic.Engine

interface Indexable {
    val index: Int
}

interface IndexableCompanion<T: Indexable> {
    fun fromIndex(index: Int): T
}
