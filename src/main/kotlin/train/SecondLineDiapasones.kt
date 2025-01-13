package train

import java.util.*

class SecondLineDiapasones {
    val timeDiapasones = mutableListOf<Pair<Date, Date>>()
    fun isInTimeDiapasone(time: Date): Boolean{
        var flag = false
        for(el in timeDiapasones){
            if(el.first < time && el.second > time){
                flag = true
                break
            }
        }
        return flag
    }
}