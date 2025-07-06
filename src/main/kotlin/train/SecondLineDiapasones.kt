package train

import java.util.*

/**
 * Хранит информацию о временных отрезках для определенного маршрута (за одни сутки)
 * @property timeDiapasons список пар со временем начала и временем конца отрезка
 * @property middleOfNight статическая константа для хранения времени полуночи 00:00:00
 */
class SecondLineDiapasones {
    val timeDiapasons = mutableListOf<Pair<Date, Date>>()
        get

    companion object{
        val middleOfNight = Calendar.Builder().setTimeOfDay(0,0,0).build().time
    }

    /**
     * Добавляет в список timeDiapasons новый временной отрезок и сортирует список
     * @param diapason временной отрезок. Если внутри отрезка оказывается полуночь,
     * добавляется 2 отрезка: до и после полуночи
     */
    fun add(diapason: Pair<Date, Date>){
        if(diapason.second < diapason.first){
            timeDiapasons.add(Pair(middleOfNight,diapason.second))
            timeDiapasons.add(Pair(diapason.first,middleOfNight))
        } else {
            timeDiapasons.add(diapason)
        }
        timeDiapasons.sortBy { if (it.first != middleOfNight && it.second != middleOfNight) it.second else it.first }
    }
}