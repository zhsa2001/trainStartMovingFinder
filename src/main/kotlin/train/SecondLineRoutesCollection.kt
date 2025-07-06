package train

import java.util.*
import kotlin.collections.HashMap

/**
 * Хранит информацию о временных отрезках маршрутов (за сутки)
 * @property routes хэш-карта с номером маршрута в качестве ключа и списком временных отрезков в качестве значения
 */
class SecondLineRoutesCollection: SavebleInfo {
    val routes = HashMap<Int, SecondLineDiapasones>()

    /**
     * Добавляет временной диапазон для маршрута в routes
     * @param route номер маршрута
     * @param diapason временной отрезок
     */
    fun addDiapason(route: Int, diapason: Pair<Date, Date>){
        if(!routes.containsKey(route)){
            routes[route] = SecondLineDiapasones()
        }
        routes[route]!!.add(diapason)
    }

    override fun info(tab: String): String {
        val s = StringBuilder()
        for(route in routes.keys){
            routes[route]!!.timeDiapasons.forEach { s.append(
                "$route: ${TrainInfo.dateTimeFormat.format(it.first)}${tab}" +
                        "${TrainInfo.dateTimeFormat.format(it.second)}\n"
            ) }
        }
        return s.toString()
    }

    override fun toString(): String {
        return info("\t")
    }
}