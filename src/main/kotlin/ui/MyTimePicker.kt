package ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import checkIsNum
import java.util.*
import kotlin.math.min

@Composable
fun MyTimePicker(hour: Int, minute: Int, setTime: (Calendar)->Unit) {
    val widthClockTextFields = 100.dp
    var hour by remember { mutableStateOf(hour.toString()) }
    var minute by remember { mutableStateOf(minute.toString()) }
    var time by remember { mutableStateOf(Calendar.Builder().set(Calendar.AM_PM, 1).setTimeOfDay(hour.toInt(),minute.toInt(),0).build())}
    setTime(time)
    Row{
        TextField(hour, onValueChange = {
            if (checkIsNum(it)) {
                hour = if(it != "" && it.toInt() > 23) "23" else it.substring(0,
                    min(2,it.length));
                if (hour != "") {
                    time.set(Calendar.HOUR_OF_DAY,hour.toInt())
                    setTime(time)
                }
            }
            }, modifier = Modifier.width(widthClockTextFields))

        Text(":", modifier = Modifier.align(Alignment.CenterVertically))
        TextField(minute, onValueChange = {
            if (checkIsNum(it)) {
                minute = if(it != "" && it.toInt() > 59) "59" else it.substring(0,
                    min(2,it.length))
                if (minute != "") {
                    time.set(Calendar.MINUTE,minute.toInt())
                    setTime(time)
                }
            } }, modifier = Modifier.width(widthClockTextFields))
    }
}
