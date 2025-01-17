package train

import java.util.*

class SecondLineDiapasones {
    val timeDiapasones = mutableListOf<Pair<Date, Date>>()
    val middleOfNight = Calendar.Builder().setTimeOfDay(0,0,0).build().time

    fun add(diapasone: Pair<Date, Date>){
        if(diapasone.second < diapasone.first){
            timeDiapasones.add(Pair(middleOfNight,diapasone.second))
            timeDiapasones.add(Pair(diapasone.first,middleOfNight))
        } else {
            timeDiapasones.add(diapasone)
        }
        timeDiapasones.sortBy { if (it.first != middleOfNight && it.second != middleOfNight) it.second else it.first }
    }
}