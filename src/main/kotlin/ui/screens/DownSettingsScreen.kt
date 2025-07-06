package ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import ui.SelectFileButton
import ui.utils.getTxtSource
import java.io.File
import java.util.*

/**
 * Экран для установки настроек для обработки нижней части от Москвы-Сити
 * @param onFileSelected файл-изображение для обработки выбран
 * @param onTimeStartSet время начала графика на изображении задано
 * @param onTimeDiapasoneInMinutesSet задана длительность временного периода графика в минутах
 * @param directory возвращает директирию для открытия, если в функцию передается null, иначе устанавливает директорию для ее сохранения
 * @param goNext функция перехода к следующему этапу
 * @param returnToStart функция возврата к начальному экрану
 * @param onFileWithUpSelected файл c информацией о верхней части-поездах от Александровского сада, для обработки выбран
 */
@Composable
fun DownSettingsScreen(onFileSelected:(File?)->Unit,
                       onTimeStartSet:(Calendar)->Unit,
                       onTimeDiapasoneInMinutesSet:(Int)->Unit,
                       direcory:(File?)->File,
                       returnToStart:()->Unit,
                       goNext:() -> Unit,
                       onFileWithUpSelected: (File?)->Unit){
    var file by remember { mutableStateOf<File?>(null) }
    Column {
        SelectFileButton("Выбрать файл с верхом", {
            it?.let{
                file = it;
                onFileWithUpSelected(file);
                direcory(file!!.parentFile)
            }
        }) { getTxtSource(direcory(null)) }

        MainSettingsScreen(
            onFileSelected,
            onTimeStartSet,
            onTimeDiapasoneInMinutesSet,
            direcory,
            returnToStart,
            goNext
        )
    }
}