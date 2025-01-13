//package ui.screens
//
//import ImageProcessing.BinaryColorSchemeConverter
//import ImageProcessing.GrayColorSchemeConverter
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.Button
//import androidx.compose.material.Text
//import androidx.compose.material.TextField
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.toComposeImageBitmap
//import androidx.compose.ui.text.TextRange
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.unit.dp
//import clearFolder
//import drawCorner
//import folderSubimages
//import getBoxes
//import getHorisontalLines
//import getSubArea
//import getWorkArea
//import kotlinx.coroutines.launch
//import resizeImage
//import train.Train
//import train.TrainSaver
//import updateRoutes2
//import java.awt.Point
//import java.awt.image.BufferedImage
//import java.io.File
//import java.util.*
//import kotlin.math.PI
//import kotlin.math.max
//import kotlin.math.min
//
//
//@Composable
//fun ProgressRouteUpScreen(image: BufferedImage, file: File, date: Calendar?, minutes: Int, goNext: () -> Unit, returnToStart:()->Unit) {
//    var corners by remember { mutableStateOf(mutableListOf<Point>()) }
//    var currentCorner by remember { mutableStateOf(-1) }
//    val scale = 2.0
//    val imageScaled = BufferedImage((image.width * scale).toInt(), (image.height/4 * scale).toInt(), BufferedImage.TYPE_INT_RGB)
//
//    var workArea by remember { mutableStateOf(0) }
//    val parts = image.width / 1500
//    val coroutineScope = rememberCoroutineScope()
//    val coroutineScopeForSendRequest = rememberCoroutineScope()
//    val scrollState = rememberScrollState()
//    val padding = 20
//
//    var textFieldVal by remember {
//        mutableStateOf(
//            TextFieldValue(
//                text = ""
//            )
//        )
//    }
//
//    val trains by remember { mutableStateOf(mutableListOf<Train>()) }
//    var platform1y by remember { mutableStateOf(image.height) }
//    var recognizedRoutes by remember { mutableStateOf(mutableListOf<String>()) }
//    LaunchedEffect(Unit){
//        corners = getBoxes(
//            BinaryColorSchemeConverter(170).convert(
//                GrayColorSchemeConverter().convert(image)))
//        corners.sortBy {it.x}
//
//
//
//
//        clearFolder(folderSubimages)
//        recognizedRoutes = MutableList<String>(corners.size,{""})
//
////        addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, "", routes, coroutineScope, scrollState)
//        for(i in 0..min(corners.size,10)){
//            if(platform1y > corners[i].y){
//                platform1y = corners[i].y
//            }
//        }
//        var y = getHorisontalLines(
//            BinaryColorSchemeConverter(threshold = 200).convert(
//                GrayColorSchemeConverter().convert(image)
//            ))[2].y
//        var countAll = corners.size
//        var countRecognized = 0
//        var currentCount = 0
//        val height = 30
////        coroutineScope.launch {
////////            var jobs = mutableListOf<Job>()
//        var job = coroutineScopeForSendRequest.launch {
//            for(i in 0..<corners.size){
//////                List<Job>(20,{
//////                break
//
//
//                currentCount++
//                recognizedRoutes[i] = (getSubArea(
//                    image, Point(corners[i].x, y), -PI * 58 / 180, Point(
//                        if (i + 1 < corners.size)
//                            corners[i + 1].x
//                        else
//                            corners[i].x + height, y
//                    )
//                ))
////                    println(recognizedRoutes[i])
//                if (recognizedRoutes[i].isNotEmpty()) {
//                    countRecognized++
//                }
//
////                val newRoutes = StringBuilder(routes)
////                routes += recognizedRoutes[i]
////                addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, routes, newRoutes, coroutineScope, scrollState, recognizedRoutes)
////                routes = newRoutes.toString()
////                currentCorner++
//            }
//
//
//        }
////            jobs.joinAll()
//
////        }
//
//        job.join()
//        println("Recognese: ${countRecognized} from ${countAll} = ${countRecognized.toDouble()/countAll*100} %")
//
//        drawCorner(image,corners[0])
//        currentCorner = 0
//        updateRoutes2(textFieldVal,textFieldVal,trains,image,corners,
//            date!!,0,minutes,platform1y,coroutineScope,scrollState,
//            recognizedRoutes)
//        textFieldVal = TextFieldValue(
//            recognizedRoutes[0], TextRange(recognizedRoutes[0].length)
//        )
//
////        currentCorner = 0
////            drawCorner(image,corners[0])
//
////            currentCorner = 0
////            val newRoutes = StringBuilder(routes)
////            addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, "", newRoutes, coroutineScope, scrollState, recognizedRoutes)
////            routes = newRoutes.toString()
//
//
//    }
//
//
//
//
//
//    Column{
//        resizeImage(imageScaled, scale, image)
//        Image(getWorkArea(imageScaled,currentCorner,workArea,parts,padding).toComposeImageBitmap(),"Область графика с началом движения")
//
//        Row {
//            Button(onClick = {
//                workArea = max(workArea - 1,0)
//            }){
//                Text("<<")
//            }
//            Button(onClick = {
//                workArea = min(workArea + 1,parts-1)
//            }){
//                Text(">>")
//            }
//        }
//        Row{
//
//            TextField(
//                value = textFieldVal, onValueChange = {
////                state.setTextAndPlaceCursorAtEnd(it)
//                    var prevCursorPosition = textFieldVal.selection.end
//                    currentCorner = updateRoutes2(it, textFieldVal, trains, image, corners, date!!, 0, minutes, platform1y,coroutineScope,scrollState,recognizedRoutes)
//                    if (it.text == textFieldVal.text+"\n"){
//                        textFieldVal = TextFieldValue(it.text+recognizedRoutes[currentCorner], TextRange(it.text.length+recognizedRoutes[currentCorner].length))
//                    } else {
//                        textFieldVal = it
//                    }
//                    drawCorner(image,corners[currentCorner]);
//                    if (prevCursorPosition == textFieldVal.text.length - 1) {
//                        workArea = corners[currentCorner].x * parts / image.width
//                    }},
//                modifier = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState).padding(0.dp,0.dp,12.dp,0.dp),
//            )
//
//
////            var state = rememberTextFieldState()
////            TextField(
////            )
//            TextField(trains.joinToString("\n"),
//                onValueChange = {},
//                enabled = false,
//                modifier = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState)
//            )
//
//        }
//
//        Button(onClick = {
//            TrainSaver(file.parent  + "/up"+ file.nameWithoutExtension + ".txt").save(trains)
//            goNext()
//        }){
//            Text("Сохранить верх")
//        }
//
//    }
//
//}
