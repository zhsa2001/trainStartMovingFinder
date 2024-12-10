import ImageProcessing.BinaryColorSchemeConverter
import ImageProcessing.CropRegion.CropRegion
import ImageProcessing.CropRegion.OldCropRegion
import ImageProcessing.GrayColorSchemeConverter
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.filechooser.UIStage
import java.awt.Checkbox
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min

@Composable
@Preview
fun App() {
    var stage by remember { mutableStateOf(UIStage.START) }
    var file by remember { mutableStateOf<File?>(null) }
    var date by remember { mutableStateOf<Calendar>(Calendar.getInstance()) }
    var message by remember { mutableStateOf("") }
    var countOfMinutes by remember { mutableStateOf(0) }

    val returnToStart:()->Unit = {stage = UIStage.START}
    MaterialTheme {

        Box(modifier = Modifier.padding(12.dp)){

            when(stage){
                UIStage.START -> MainScreen(
                    {
                        it?.let { file = it }
                    },
                    {
                        date = it
                    },
                    {
                        countOfMinutes = it
                    },
                    { stage = UIStage.ROUTE_PROCESSING }
                )

                UIStage.ROUTE_PROCESSING -> {
                    ProgressRouteScreen(
                        ImageIO.read(file!!),
                        file!!,
                        date,
                        countOfMinutes,
                        { stage = UIStage.DONE },
                        { returnToStart() }
                    )
                }
                UIStage.DONE -> ResultOfCropping(message,
                    { returnToStart() }
                )
            }

        }
    }
}

@Composable
fun ProgressRouteScreen(image: BufferedImage, file: File, date: Calendar?, minutes: Int, goNext: () -> Unit, returnToStart:()->Unit) {
    var corners by remember { mutableStateOf(mutableListOf<Point>())}
    var currentCorner by remember { mutableStateOf(-1) }
    val scale = 2.0
    val imageScaled = BufferedImage((image.width * scale).toInt(), (image.height/4 * scale).toInt(), BufferedImage.TYPE_INT_RGB)
    var routes by remember { mutableStateOf("") }

    var workArea by remember { mutableStateOf(0)}
    val parts = 20
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val padding = 20

    val trains by remember { mutableStateOf(mutableListOf<Train>()) }
    var platform1y by remember { mutableStateOf(image.height) }
    LaunchedEffect(null){
        corners = getBoxes(
            BinaryColorSchemeConverter(170).convert(
            GrayColorSchemeConverter().convert(image)))
        corners.sortBy {it.x}
        drawCorner(image,corners[0])

        currentCorner = 0
        addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, "", routes, coroutineScope, scrollState)
        for(i in 0..min(corners.size,10)){
            if(platform1y > corners[i].y){
                platform1y = corners[i].y
            }
        }
    }





    Column{
        resizeImage(imageScaled, scale, image)
        Image(getWorkArea(imageScaled,currentCorner,workArea,parts,padding).toComposeImageBitmap(),"Область графика с началом движения")

        Row {
            Button(onClick = {
                workArea = max(workArea - 1,0)
            }){
                Text("<<")
            }
            Button(onClick = {
                workArea = min(workArea + 1,parts-1)
            }){
                Text(">>")
            }
        }
        Row{
            TextField(routes, onValueChange = {
                val routesOld = routes;
                routes = it;
                currentCorner = addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, routesOld, routes, coroutineScope, scrollState)
                updateRoutes(trains, currentCorner, routes)
                drawCorner(image,corners[currentCorner]);
                workArea = corners[currentCorner].x * parts / image.width },
                modifier = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState).padding(0.dp,0.dp,12.dp,0.dp))

            TextField(trains.joinToString("\n"),
                onValueChange = {},
                enabled = false,
                modifier = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState)
            )

        }

        Button(onClick = {
            TrainSaver(file.parent  + "/"+ file.nameWithoutExtension + ".txt").save(trains)
            goNext()
        }){
            Text("Save")
        }

    }

}



@Composable
fun MainScreen(onFileSelected:(File?)->Unit,
               onTimeStartSet:(Calendar)->Unit,
               onTimeDiapasoneInMinutesSet:(Int)->Unit,
               goNext:() -> Unit){


    var hour by remember { mutableStateOf("5") }
    var minute by remember { mutableStateOf("30") }
    var hourEnd by remember { mutableStateOf("5") }
    var minuteEnd by remember { mutableStateOf("30") }
    var file by remember { mutableStateOf<File?>(null) }
    val widthClockTextFields = 100.dp
    var nextDay by remember { mutableStateOf(true) }

    Column(){
        Row{
            Button(onClick = {
                file = getImageSource()
            }) {
                Text("Выбрать файл")
            }
            file?.let { Text(it.name,modifier = Modifier.align(Alignment.CenterVertically),fontStyle = FontStyle.Italic) }
        }
        Text("Время начала")
        Row{

            TextField(hour, onValueChange = { if (checkIsNum(it)) hour = if(it != "" && it.toInt() > 23) "23" else it.substring(0,min(2,it.length)) }, modifier = Modifier.width(widthClockTextFields))
            Text(":", modifier = Modifier.align(Alignment.CenterVertically))
            TextField(minute, onValueChange = { if (checkIsNum(it)) minute = if(it != "" && it.toInt() > 59) "59" else it.substring(0,min(2,it.length)) }, modifier = Modifier.width(widthClockTextFields))
        }
        Text("Время конца")
        Row{
            TextField(hourEnd, onValueChange = { if (checkIsNum(it)) hourEnd = if(it != "" && it.toInt() > 23) "23" else it.substring(0,min(2,it.length)) }, modifier = Modifier.width(widthClockTextFields))
            Text(":", modifier = Modifier.align(Alignment.CenterVertically))
            TextField(minuteEnd, onValueChange = { if (checkIsNum(it)) minuteEnd = if(it != "" && it.toInt() > 59) "59" else it.substring(0,min(2,it.length)) }, modifier = Modifier.width(widthClockTextFields))
        }
        Row {
            Checkbox(checked = nextDay, onCheckedChange = {nextDay = it})
            Text("Захватывается начало следующего дня", modifier = Modifier.align(Alignment.CenterVertically))
        }

        file?.let{
            Button(onClick =
            {
                onFileSelected(file)
                onTimeStartSet(Calendar.Builder().setTimeOfDay(hour.toInt(),minute.toInt(),0).build())
                val minutesRange = hourEnd.toInt()*60+minuteEnd.toInt() - (hour.toInt()*60+minute.toInt()) + if (nextDay) 24*60 else 0
                onTimeDiapasoneInMinutesSet(minutesRange)
                goNext() }){
                Text("Продолжить")
            }
        }
    }
}

@Composable
fun ResultOfCropping(message: String, returnToStart:()->Unit){
    Column(

    ) {
        Column{
            Button(onClick = {
                returnToStart()
            }
            ) {
                Text("Вернуться в начало")
            }
        }
        Column{
            val state = rememberScrollState()
            LaunchedEffect(Unit) { state.animateScrollTo(100) }
            Text(message,Modifier.verticalScroll(state))
        }


    }
}

fun main() = application {
    val AppIcon = painterResource("icon.png")
    Window(onCloseRequest = ::exitApplication, icon = AppIcon, title = "Обнаружение точек старта движения для табло") {
        App()
    }
}

fun getImageSource(): File? = getFileFromChooseDialog()