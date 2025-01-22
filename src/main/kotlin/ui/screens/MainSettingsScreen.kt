package ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ui.MyTimePicker
import ui.SelectFileButton
import ui.utils.getImageSource
import java.io.File
import java.util.*

@Composable
fun MainSettingsScreen(onFileSelected:(File?)->Unit,
                       onTimeStartSet:(Calendar)->Unit,
                       onTimeDiapasoneInMinutesSet:(Int)->Unit,
                       direcory:(File?)->File,
                       returnToStart:()->Unit,
                       goNext:() -> Unit){

    var timeStart by remember { mutableStateOf(Calendar.Builder().build()) }
    var timeEnd by remember { mutableStateOf(Calendar.Builder().build()) }
    var file by remember { mutableStateOf<File?>(null) }
    var nextDay by remember { mutableStateOf(true) }

    Column{
        SelectFileButton("Выбрать изображение",
            {
                it?.let{
                    file = it;
                    onFileSelected(file);
                    direcory(file!!.parentFile)
                }
            },
            { getImageSource(direcory(null)) })

        Text("Время начала")
        MyTimePicker(5,30, { timeStart = it })

        Text("Время конца")
        MyTimePicker(5,30, { timeEnd = it })
        Row {
            Checkbox(checked = nextDay, onCheckedChange = {nextDay = it})
            Text("Захватывается начало следующего дня", modifier = Modifier.align(Alignment.CenterVertically))
        }

        file?.let{
            Button(onClick =
            {
                onTimeStartSet(timeStart)
                val minutesRange = timeEnd[Calendar.HOUR_OF_DAY]*60+timeEnd[Calendar.MINUTE] - (timeStart[Calendar.HOUR_OF_DAY]*60+timeStart[Calendar.MINUTE]) + if (nextDay) 24*60 else 0
                onTimeDiapasoneInMinutesSet(minutesRange)
                goNext() }){
                Text("Продолжить")
            }
        }
        Button(onClick = returnToStart){
            Text("Назад")
        }
    }
}