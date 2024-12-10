import java.text.SimpleDateFormat
import java.util.*

class Train {
    var route: Int = 0
    var time: Date = Date()
    var isGoingToDepo = false
    var platform = 1

    fun info(tab: String = "\t"): String {
        val dateTimeFormat = SimpleDateFormat("HH:mm:ss")
        return "${dateTimeFormat.format(time)}$tab${route}$tab${platform}$tab${if (isGoingToDepo) 1 else 0}"
    }

    override fun toString(): String {
        val tab = " "
        val dateTimeFormat = SimpleDateFormat("HH:mm:ss")
        return "${dateTimeFormat.format(time)}$tab${route}$tab${platform}$tab${if (isGoingToDepo) 1 else 0}"
    }
}