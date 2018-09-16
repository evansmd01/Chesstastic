package chesstastic.engine.entities

data class Square(val file: File, val rank: Rank) {

    fun transform(fileDelta: Int, rankDelta: Int): Square? {
        val newFile = File.fromIndex(file.index + fileDelta)
        val newRank = Rank.fromIndex(rank.index + rankDelta)
        return if (newFile != null && newRank != null) Square(newFile, newRank)
        else null
    }

    companion object {
        fun parse(input: String): Square? {
            val pattern = """^([A-H])([1-8])$""".toRegex()
            val matchResult = pattern.matchEntire(input.toUpperCase())
            if (matchResult != null) {
                val (file, rank) = matchResult.destructured
                return Square(File.valueOf(file), Rank.fromIndex(rank.toInt() - 1)!!)
            }
            return null
        }
    }
}

