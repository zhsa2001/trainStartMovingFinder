package train

import java.text.SimpleDateFormat
import java.util.*

class Train {
    var route: Int = 0
    var time: Date = Date()
    var isGoingToDepo = false
    var platform = 1
    val dateTimeFormat = SimpleDateFormat("HH:mm:ss")

    fun info(tab: String = "\t"): String {
        return "${dateTimeFormat.format(time)}$tab${platform}$tab${route}$tab${if (isGoingToDepo) 1 else 0}"
    }

    override fun toString(): String {
        val tab = " "
        return info(tab)
    }
}