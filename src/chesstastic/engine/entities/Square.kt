package chesstastic.engine.entities

data class Square(val file: File, val rank: Rank) {

    fun transform(fileDelta: Int, rankDelta: Int): Square? {
        val newFile = File.fromIndex(file.index + fileDelta)
        val newRank = Rank.fromIndex(rank.index + rankDelta)
        return if (newFile != null && newRank != null) Square(newFile, newRank)
        else null
    }

    fun pathTo(target: Square): List<Square> {
        return if (this != target) {
            val next = moveTowards(target) ?: throw Exception("unable to move from $this towards $target")
            pathTo(next) + next
        } else emptyList()
    }

    private fun moveTowards(target: Square): Square? =
        when {
            // move up file
            target.rank == rank && target.file > file -> transform(1, 0)

            // move down file
            target.rank == rank && target.file < file -> transform(-1, 0)

            // move up rank
            target.file == file && target.rank > rank -> transform(0, 1)

            // move down rank
            target.file == file && target.rank < rank -> transform(0, -1)

            // move up file, up rank
            target.file > file && target.rank > rank -> transform(1, 1)

            // move up file, down rank
            target.file > file && target.rank < rank -> transform(1, -1)

            // move down file, up rank
            target.file < file && target.rank > rank -> transform(-1, 1)

            // move down file, down rank
            target.file < file && target.rank < rank -> transform(-1, -1)

            else -> null
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

