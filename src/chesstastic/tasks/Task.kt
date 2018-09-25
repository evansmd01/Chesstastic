package chesstastic.tasks

interface Task {
    val name: String
    fun execute()

    companion object {
        private val taskMap = listOf(
            GameGenerator
        ).map { GameGenerator.name.toLowerCase() to it }.toMap()

        fun get(name: String): Task? = taskMap[name.toLowerCase()]
    }
}
