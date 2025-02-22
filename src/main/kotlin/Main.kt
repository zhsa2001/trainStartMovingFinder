import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import train.Train
import ui.UIStage
import ui.screens.*
import java.io.File
import java.util.*


val folderSubimages = "subimages"
val asposeFolder = "aspose_data"


@Composable
@Preview
fun App() {
    var stage by remember { mutableStateOf(UIStage.START) }
    var file by remember { mutableStateOf<File?>(null) }
    var date by remember { mutableStateOf<Calendar>(Calendar.getInstance()) }
    var message by remember { mutableStateOf("") }
    var countOfMinutes by remember { mutableStateOf(0) }
    var trains by remember { mutableStateOf(mutableListOf<Train>()) }


    var previousPath by remember { mutableStateOf(File("")) }

    val setPreviousPath:(File?)->File = {
        it?.let {
            previousPath = it
        }
        previousPath
    }


    val returnToStart:()->Unit = {stage = UIStage.START}
    MaterialTheme {

        Box(modifier = Modifier.padding(12.dp)){
            when(stage){
                UIStage.START -> StartScreen({ stage = UIStage.SETTINGS_PROCESSING_UP},
                    { stage = UIStage.SETTINGS_PROCESSING_DOWN})
                UIStage.SETTINGS_PROCESSING_UP -> MainSettingsScreen(
                    { it?.let { file = it } },
                    { date = it },
                    { countOfMinutes = it },
                    setPreviousPath,
                    returnToStart,
                    { stage = UIStage.ROUTE_PROCESSING_UP }
                )
                UIStage.SETTINGS_PROCESSING_DOWN -> DownSettingsScreen(
                    { it?.let { file = it } },
                    { date = it },
                    { countOfMinutes = it },
                    setPreviousPath,
                    returnToStart,
                    { stage = UIStage.ROUTE_PROCESSING_DOWN },
                    { it?.let { trains = getTrainsFromFile(it) } }
                )

                UIStage.ROUTE_PROCESSING_UP -> {
                    UpPartProgressScreen(
                        file!!,
                        date,
                        countOfMinutes,
                        { stage = UIStage.DONE },
                        returnToStart,
                        { message = it }
                    )
                }
                UIStage.ROUTE_PROCESSING_DOWN -> {
                    DownPartProgressScreen(
                        file!!,
                        date,
                        countOfMinutes,
                        {
                            stage = UIStage.DONE;
                            trains.clear()
                        },
                        returnToStart,
                        { message = it },
                        trains
                    )
                }
                UIStage.DONE -> {
                    ResultScreen(message,
                        returnToStart
                    )
                }
            }
        }
    }
}

fun main() = application {
    val AppIcon = painterResource("icon.png")
    Window(onCloseRequest = ::exitApplication, icon = AppIcon, title = "Обнаружение точек старта движения для табло") {
        App()
    }
}