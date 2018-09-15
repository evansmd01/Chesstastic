package chestastic.engine

data class Coordinate(val file: File, val rank: Rank) {
    fun indexEquals(fileIndex: Int, rankIndex: Int): Boolean =
            fileIndex == file.index && rankIndex == rank.index

    companion object {
        fun parse(input: String): Coordinate? {
            val pattern = """^([A-H])([1-8])$""".toRegex()
            val matchResult = pattern.matchEntire(input.toUpperCase())
            if (matchResult != null) {
                val (file, rank) = matchResult.destructured
                return Coordinate(File.valueOf(file), Rank.valueOf(rank.toInt()))
            }
            return null
        }
    }
}
