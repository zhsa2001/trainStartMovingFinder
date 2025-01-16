package train

import java.util.*
import kotlin.collections.HashMap

class SecondLineRoutesCollection: SavebleInfo {
    val routes = HashMap<Int, SecondLineDiapasones>()
    fun addDiapasone(route: Int, diapasone: Pair<Date, Date>){
        if(!routes.containsKey(route)){
            routes[route] = SecondLineDiapasones()
        }
        routes[route]!!.timeDiapasones.add(diapasone)
    }
    fun inDiapasone(route: Int, time: Date): Boolean{
        return routes.containsKey(route) && routes[route]!!.isInTimeDiapasone(time)
    }

    override fun info(tab: String): String {
        return toString()
    }

    override fun toString(): String {
        var s = StringBuilder()
        for(route in routes.keys){
            routes[route]!!.timeDiapasones.forEach { s.append(
                "$route: ${Train().dateTimeFormat.format(it.first)}\t" +
                        "${Train().dateTimeFormat.format(it.second)}\n"
            ) }
        }
        return s.toString()
    }
}