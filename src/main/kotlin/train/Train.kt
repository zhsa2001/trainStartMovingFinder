package train

import java.text.SimpleDateFormat
import java.util.*

/**
 * Информация о поезде на крайней станции
 * @property route номер маршрута
 * @property time время отправления
 * @property isGoingToDepo флаг ухода в депо (если true, то уходит)
 * @property platform номер платформы отправления
 * @property dateTimeFormat формат для времени
 */
class TrainInfo: SavebleInfo {
    var route: Int = 0
    var time: Date = Date()
    var isGoingToDepo = false
    var platform = 1
    companion object {
        val dateTimeFormat = SimpleDateFormat("HH:mm:ss")
    }

    override fun info(tab: String): String {
        return "${dateTimeFormat.format(time)}$tab${platform}$tab${route}$tab${if (isGoingToDepo) 1 else 0}"
    }

    override fun toString(): String {
        val tab = " "
        return info(tab)
    }
}