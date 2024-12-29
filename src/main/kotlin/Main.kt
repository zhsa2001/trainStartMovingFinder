import ImageProcessing.BinaryColorSchemeConverter
import ImageProcessing.GrayColorSchemeConverter
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import ui.filechooser.UIStage
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min
import androidx.compose.ui.text.input.TextFieldValue


val folderSubimages = "subimages"


@Composable
@Preview
fun App() {
    var stage by remember { mutableStateOf(UIStage.START) }
    var file by remember { mutableStateOf<File?>(null) }
    var date by remember { mutableStateOf<Calendar>(Calendar.getInstance()) }
    var message by remember { mutableStateOf("") }
    var countOfMinutes by remember { mutableStateOf(0) }
    var trains by remember { mutableStateOf(mutableListOf<Train>()) }

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
                    {
                        stage = UIStage.ROUTE_PROCESSING_UP
                    }
                )

                UIStage.ROUTE_PROCESSING_UP -> {
                    ProgressRouteUpScreen(
                        ImageIO.read(file!!),
                        file!!,
                        date,
                        countOfMinutes,
                        { stage = UIStage.ROUTE_PROCESSING_DOWN },
                        { returnToStart() },
                        { trains = it  }
                    )
                }
                UIStage.ROUTE_PROCESSING_DOWN -> {
                    ProgressRouteDownScreen(
                        ImageIO.read(file!!),
                        file!!,
                        date,
                        countOfMinutes,
                        { stage = UIStage.DONE },
                        { returnToStart() },
                        trains
                    )
                }
                UIStage.CHECK_ROUTE -> CheckRouteScreen(
                    ImageIO.read(file!!),
                    file!!,
                    date,
                    countOfMinutes,
                    { stage = UIStage.CHECK_ROUTE },
                    { returnToStart() },
                    { trains = it  }
                )
                UIStage.DONE -> ResultOfCropping(message,
                    { returnToStart() }
                )
            }

        }
    }
}

@Composable
fun ProgressRouteDownScreen(image: BufferedImage, file: File, date: Calendar?, minutes: Int, goNext: () -> Unit, returnToStart:()->Unit, trains: MutableList<Train>) {
    var corners by remember { mutableStateOf(mutableListOf<Point>())}
    var currentCorner by remember { mutableStateOf(-1) }
    val scale = 2.0
    val imageScaled = BufferedImage((image.width * scale).toInt(), (image.height/4 * scale).toInt(), BufferedImage.TYPE_INT_RGB)
    var workArea by remember { mutableStateOf(0)}
    val parts = image.width / 1500
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val padding = 20
    val trains2 by remember { mutableStateOf(mutableListOf<Train>()) }

    var textFieldVal by remember {
        mutableStateOf(
            TextFieldValue(
                text = ""
            )
        )
    }

    LaunchedEffect(Unit){
        corners = getBoxes2(
            BinaryColorSchemeConverter(170).convert(
                GrayColorSchemeConverter().convert(image)))

        corners.sortBy {it.x}

        currentCorner = -1
//        drawCorner(image,corners[0])

    }

    Column{
        resizeImage(imageScaled, scale, image.getSubimage(0,image.height*3/4,image.width,image.height/4))
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
        Button(onClick = {
//            currentCorner++
            while(currentCorner < corners.size-1){
                currentCorner = addTrainIfNeeded2(trains2,image, corners, date!!, 0, minutes)
                currentCorner = min(currentCorner, corners.size-1)
//                drawCorner(image,corners[currentCorner])
//                println(trains2[currentCorner])
            }

        }){
            Text("press")
        }
        Button(onClick = {

        }){
            Text("save")
        }
        /*
        Row{

            TextField(
                value = textFieldVal, onValueChange = {
//                state.setTextAndPlaceCursorAtEnd(it)
                    currentCorner = updateRoutes2(it, textFieldVal, trains, image, corners, date!!, 0, minutes, platform1y,coroutineScope,scrollState,recognizedRoutes)
                    if (it.text == textFieldVal.text+"\n"){
                        textFieldVal = TextFieldValue(it.text+recognizedRoutes[currentCorner], TextRange(it.text.length+recognizedRoutes[currentCorner].length))
                    } else {
                        textFieldVal = it
                    }
                    drawCorner(image,corners[currentCorner]);
                    workArea = corners[currentCorner].x * parts / image.width },
                modifier = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState).padding(0.dp,0.dp,12.dp,0.dp),
            )


//            var state = rememberTextFieldState()
//            TextField(
//            )
            TextField(trains.joinToString("\n"),
                onValueChange = {},
                enabled = false,
                modifier = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState)
            )

        }

         */

        Button(onClick = {
            TrainSaver(file.parent  + "/down"+ file.nameWithoutExtension + ".txt").save(trains2)
            formListTrainsInSecondLine(trains2)
            goNext()
        }){
            Text("Сохранить низ")
        }



    }
}

    @OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProgressRouteUpScreen(image: BufferedImage, file: File, date: Calendar?, minutes: Int, goNext: () -> Unit, returnToStart:()->Unit, setTrains: (MutableList<Train>) -> Unit) {
    var corners by remember { mutableStateOf(mutableListOf<Point>())}
    var currentCorner by remember { mutableStateOf(-1) }
    val scale = 2.0
    val imageScaled = BufferedImage((image.width * scale).toInt(), (image.height/4 * scale).toInt(), BufferedImage.TYPE_INT_RGB)

    var workArea by remember { mutableStateOf(0)}
    val parts = image.width / 1500
    val coroutineScope = rememberCoroutineScope()
    val coroutineScopeForSendRequest = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val padding = 20

    var textFieldVal by remember {
        mutableStateOf(
            TextFieldValue(
                text = ""
            )
        )
    }

    val trains by remember { mutableStateOf(mutableListOf<Train>()) }
    var platform1y by remember { mutableStateOf(image.height) }
    var recognizedRoutes by remember { mutableStateOf(mutableListOf<String>()) }
    LaunchedEffect(Unit){
        corners = getBoxes(
            BinaryColorSchemeConverter(170).convert(
                GrayColorSchemeConverter().convert(image)))
        corners.sortBy {it.x}




        clearFolder(folderSubimages)
        recognizedRoutes = MutableList<String>(corners.size,{""})
        currentCorner = 0
//        addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, "", routes, coroutineScope, scrollState)
        for(i in 0..min(corners.size,10)){
            if(platform1y > corners[i].y){
                platform1y = corners[i].y
            }
        }
        var y = getHorisontalLines(
            BinaryColorSchemeConverter(threshold = 200).convert(
                GrayColorSchemeConverter().convert(image)
            ))[2].y
        var countAll = corners.size
        var countRecognized = 0
        var currentCount = 0
        val height = 30
//        runBlocking {
////            var jobs = mutableListOf<Job>()
//            for(i in 0..<corners.size){
////                List<Job>(20,{
////                break
////                jobs.add(coroutineScopeForSendRequest.launch {
//
//                    currentCount++
//                    recognizedRoutes[i] = (getSubArea(
//                        image, Point(corners[i].x, y), -PI * 58 / 180, Point(
//                            if (i + 1 < corners.size)
//                                corners[i + 1].x
//                            else
//                                corners[i].x + height, y
//                        )
//                    ))
////                    println(recognizedRoutes[i])
//                    if (recognizedRoutes[i].isNotEmpty()) {
//                        countRecognized++
//                    }
//
////                val newRoutes = StringBuilder(routes)
////                routes += recognizedRoutes[i]
////                addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, routes, newRoutes, coroutineScope, scrollState, recognizedRoutes)
////                routes = newRoutes.toString()
////                currentCorner++
////                })
//
//
//            }
////            jobs.joinAll()
//
//        }
//        println("Recognese: ${countRecognized} from ${countAll} = ${countRecognized.toDouble()/countAll*100} %")

//        job.join()
        drawCorner(image,corners[0])
        updateRoutes2(textFieldVal,textFieldVal,trains,image,corners,
            date!!,0,minutes,platform1y,coroutineScope,scrollState,
            recognizedRoutes)
        textFieldVal = TextFieldValue(
            recognizedRoutes[0],TextRange(recognizedRoutes[0].length)
        )

//        currentCorner = 0
//            drawCorner(image,corners[0])

//            currentCorner = 0
//            val newRoutes = StringBuilder(routes)
//            addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, "", newRoutes, coroutineScope, scrollState, recognizedRoutes)
//            routes = newRoutes.toString()


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

            TextField(
                value = textFieldVal, onValueChange = {
//                state.setTextAndPlaceCursorAtEnd(it)
                    currentCorner = updateRoutes2(it, textFieldVal, trains, image, corners, date!!, 0, minutes, platform1y,coroutineScope,scrollState,recognizedRoutes)
                    if (it.text == textFieldVal.text+"\n"){
                        textFieldVal = TextFieldValue(it.text+recognizedRoutes[currentCorner], TextRange(it.text.length+recognizedRoutes[currentCorner].length))
                    } else {
                        textFieldVal = it
                    }
                    drawCorner(image,corners[currentCorner]);
                    workArea = corners[currentCorner].x * parts / image.width },
                modifier = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState).padding(0.dp,0.dp,12.dp,0.dp),
            )


//            var state = rememberTextFieldState()
//            TextField(
//            )
            TextField(trains.joinToString("\n"),
                onValueChange = {},
                enabled = false,
                modifier = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState)
            )

        }

        Button(onClick = {
//            TrainSaver(file.parent  + "/up"+ file.nameWithoutExtension + ".txt").save(trains)
            setTrains(trains)
            formListTrainsInSecondLine(trains)
            goNext()
        }){
            Text("Сохранить верх")
        }

    }

}


@Composable
fun CheckRouteScreen(image: BufferedImage, file: File, date: Calendar?, minutes: Int, goNext: () -> Unit, returnToStart:()->Unit, setTrains: (MutableList<Train>) -> Unit) {
    var corners by remember { mutableStateOf(mutableListOf<Point>())}
    var currentCorner by remember { mutableStateOf(-1) }
    val scale = 2.0
    val imageScaled = BufferedImage((image.width * scale).toInt(), (image.height/4 * scale).toInt(), BufferedImage.TYPE_INT_RGB)
    var routes by remember { mutableStateOf("") }

    var workArea by remember { mutableStateOf(0)}
    val parts = image.width / 1500
//    println(image.width / 20)
    val coroutineScope = rememberCoroutineScope()
    val coroutineScopeForSendRequest = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val cursorState = rememberCursorPositionProvider()
    val padding = 20
    var curProgress by remember { mutableStateOf(0) }

    val trains by remember { mutableStateOf(mutableListOf<Train>()) }
    var platform1y by remember { mutableStateOf(image.height) }
    var recognizedRoutes by remember { mutableStateOf(mutableListOf<String>()) }
    LaunchedEffect(Unit){
        corners = getBoxes(
            BinaryColorSchemeConverter(170).convert(
                GrayColorSchemeConverter().convert(image)))
        corners.sortBy {it.x}
        drawCorner(image,corners[0])


        clearFolder(folderSubimages)

        currentCorner = 0
//        addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, "", routes, coroutineScope, scrollState)
        for(i in 0..min(corners.size,10)){
            if(platform1y > corners[i].y){
                platform1y = corners[i].y
            }
        }
        var y = getHorisontalLines(
            BinaryColorSchemeConverter(threshold = 200).convert(
                GrayColorSchemeConverter().convert(image)
            ))[2].y
//        val job = coroutineScopeForSendRequest.launch {
//            var countAll = corners.size
//            var countRecognized = 0
//            val height = 30
//            for(i in corners.indices){
//                recognizedRoutes.add(getSubArea(image,Point(corners[i].x,y),-PI * 58/180,Point(
//                    if (i + 1 < corners.size)
//                        corners[i+1].x
//                    else
//                        corners[i].x + height
//                    ,y)))
//                println(recognizedRoutes[i])
//                if (recognizedRoutes[i].isNotEmpty()){
//                    countRecognized++
//                }
        val newRoutes = StringBuilder(routes)
//                routes += recognizedRoutes[i]
        addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, routes, newRoutes, coroutineScope, scrollState, recognizedRoutes)
        routes = newRoutes.toString()
        currentCorner++
//            }
//            println("Recognese: ${countRecognized} from ${countAll} = ${countRecognized.toDouble()/countAll*100} %")

//        }
//        job.join()

        currentCorner = 0
//            drawCorner(image,corners[0])

//            currentCorner = 0
//            val newRoutes = StringBuilder(routes)
//            addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, "", newRoutes, coroutineScope, scrollState, recognizedRoutes)
//            routes = newRoutes.toString()


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
//                rememberCursorPositionProvider()
                val newRoutes = StringBuilder(it)
//                coroutineScopeForSendRequest.launch {
                currentCorner = addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, routesOld, newRoutes, coroutineScope, scrollState, recognizedRoutes)
                routes = newRoutes.toString()
//                }
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
            setTrains(trains)
            formListTrainsInSecondLine(trains)
//            goNext()
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
                onTimeStartSet(Calendar.Builder().set(Calendar.AM_PM, 1).setTimeOfDay(hour.toInt(),minute.toInt(),0).build())
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