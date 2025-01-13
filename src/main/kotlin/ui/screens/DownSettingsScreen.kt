package ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import ui.SelectFileButton
import ui.utils.getTxtSource
import java.io.File
import java.util.*

@Composable
fun DownSettingsScreen(onFileSelected:(File?)->Unit,
                       onTimeStartSet:(Calendar)->Unit,
                       onTimeDiapasoneInMinutesSet:(Int)->Unit,
                       goNext:() -> Unit,
                       onFileWithUpSelected: (File?)->Unit){
    Column {
        SelectFileButton("Выбрать файл с верхом", onFileWithUpSelected) { getTxtSource() }

        MainSettingsScreen(
            onFileSelected,
            onTimeStartSet,
            onTimeDiapasoneInMinutesSet,
            goNext
        )
    }
}