package train

import java.util.*
import kotlin.collections.HashMap

class SecondLineRoutesCollection {
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

    override fun toString(): String {
        var s = StringBuilder()
        for(route in routes.keys){
            s.append("{$route: ")
            routes[route]!!.timeDiapasones.forEach { s.append(
                "$it; "
            ) }
        }
        return s.toString()
    }
}