package ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import ui.SelectFileButton
import ui.utils.getTxtSource
import java.io.File
import java.util.*

@Composable
fun DownSettingsScreen(onFileSelected:(File?)->Unit,
                       onTimeStartSet:(Calendar)->Unit,
                       onTimeDiapasoneInMinutesSet:(Int)->Unit,
                       direcory:(File?)->File,
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
            goNext
        )
    }
}