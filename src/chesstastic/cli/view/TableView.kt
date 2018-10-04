package chesstastic.cli.view

import chesstastic.ai.models.Score
import chesstastic.engine.entities.Color
import chesstastic.util.applyColor

abstract class TableView {
    fun Score.format(): String {
        val lightString = light.format()
        val darkString = dark.format()
        return when {
            light > dark -> "${lightString.applyColor(Color.Light)} to $darkString"
            light < dark -> "$lightString to ${darkString.applyColor(Color.Dark)}"
            else -> "$lightString to $darkString"
        }
    }

    fun Double.format() = "%.2f".format(this)

    val indent = "   "
    val separator = "|  "
}
