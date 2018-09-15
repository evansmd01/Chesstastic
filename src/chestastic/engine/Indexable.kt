package chestastic.engine

interface Indexable {
    val index: Int
}

interface IndexableCompanion<T: Indexable> {
    fun fromIndex(index: Int): T
}
