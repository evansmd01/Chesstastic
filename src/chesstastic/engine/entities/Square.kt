package chesstastic.engine.entities

data class Square(val file: File, val rank: Rank) {

    fun transform(fileDelta: Int, rankDelta: Int): Square? {
        val newFile = File.fromIndex(file.index + fileDelta)
        val newRank = Rank.fromIndex(rank.index + rankDelta)
        return if (newFile != null && newRank != null) Square(newFile, newRank)
        else null
    }

    fun findPathTo(target: Square): List<Square> {
        return if (this != target) {
            val next = moveTowards(target) ?: throw Exception("unable to move from $this towards $target")
            next.findPathTo(target) + next
        } else emptyList()
    }

    fun moveAwayFrom(target: Square): Square? = moveTowards(target, -1)

    private fun moveTowards(target: Square, steps: Int = 1): Square? =
        when {
            // move up file
            target.rank == rank && target.file > file -> transform(steps, 0)
            // move down file
            target.rank == rank && target.file < file -> transform(-steps, 0)
            // move up rank
            target.file == file && target.rank > rank -> transform(0, steps)
            // move down rank
            target.file == file && target.rank < rank -> transform(0, -steps)
            // move up file, up rank
            target.file > file && target.rank > rank -> transform(steps, steps)
            // move up file, down rank
            target.file > file && target.rank < rank -> transform(steps, -steps)
            // move down file, up rank
            target.file < file && target.rank > rank -> transform(-steps, steps)
            // move down file, down rank
            target.file < file && target.rank < rank -> transform(-steps, -steps)
            else -> null
        }
}

