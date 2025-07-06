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
/**
 * Часть экрана с общими настройками для обеих частей графика: от Александровского сада и от Москвы-сити
 * @param onFileSelected файл-изображение для обработки выбран
 * @param onTimeStartSet время начала графика на изображении задано
 * @param onTimeDiapasonInMinutesSet задана длительность временного периода графика в минутах
 * @param directory возвращает директирию для открытия, если в функцию передается null, иначе устанавливает директорию для ее сохранения
 * @param goNext функция перехода к следующему этапу
 * @param returnToStart функция возврата к начальному экрану
 */
@Composable
fun MainSettingsScreen(onFileSelected:(File?)->Unit,
                       onTimeStartSet:(Calendar)->Unit,
                       onTimeDiapasonInMinutesSet:(Int)->Unit,
                       directory:(File?)->File,
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
                    directory(file!!.parentFile)
                }
            },
            { getImageSource(directory(null)) })

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
                    onTimeDiapasonInMinutesSet(minutesRange)
                    goNext() }){
                Text("Продолжить")
            }
        }
        Button(onClick = returnToStart){
            Text("Назад")
        }
    }
}