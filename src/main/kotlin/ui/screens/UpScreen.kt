package ui.screens

import ImageProcessing.BinaryColorSchemeConverter
import ImageProcessing.GrayColorSchemeConverter
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import clearFolder
import drawCorner
import folderSubimages
import getBoxes
import getHorisontalLines
import getSubArea
import io.ktor.utils.io.*
import io.ktor.utils.io.locks.*
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import train.Train
import updateRoutes2
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import kotlin.math.PI
import kotlin.math.min

@OptIn(InternalAPI::class)
@Composable
fun UpPartProgressScreen(image: BufferedImage, file: File, date: Calendar?, minutes: Int, goNext: () -> Unit, returnToStart:()->Unit, returnMessage: (String)-> Unit,) {
    val trains by remember { mutableStateOf(mutableListOf<Train>()) }
    var platform1y by remember { mutableStateOf(image.height) }
    var recognizedRoutes by remember { mutableStateOf(mutableListOf<String>()) }

    var corners by remember { mutableStateOf(mutableListOf<Point>()) }
    var currentCorner by remember { mutableStateOf(-1) }

    val coroutineScopeForSendRequest = rememberCoroutineScope()

    var textFieldVal by remember {
        mutableStateOf(
            TextFieldValue(
                text = ""
            )
        )
    }

    LaunchedEffect(Unit){
        corners = getBoxes(
            BinaryColorSchemeConverter(170).convert(
                GrayColorSchemeConverter().convert(image)))
        corners.sortBy {it.x}

        clearFolder(folderSubimages)
        recognizedRoutes = MutableList<String>(corners.size,{""})

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
//        var currentCount = 0
        val height = 30
//        coroutineScope.launch {
//////            var jobs = mutableListOf<Job>()
        var job = coroutineScopeForSendRequest.launch {
            val dispatcher = Dispatchers.Default.limitedParallelism(3)
            println("asu")
            for(i in 0..<corners.size){
////                List<Job>(20,{
////                break
                launch(dispatcher) {
//                    println("$i ${Thread.currentThread().name}")
//                    currentCount++
                    recognizedRoutes[i] = (getSubArea(
                        image, Point(corners[i].x, y), -PI * 58 / 180, Point(
                            if (i + 1 < corners.size)
                                corners[i + 1].x
                            else
                                corners[i].x + height, y
                        )
                    ))
//                    println(recognizedRoutes[i])
                    if (recognizedRoutes[i].isNotEmpty()) {
                        synchronized(countRecognized){
                            countRecognized++
                        }
                    }
                }
            }


        }
//            jobs.joinAll()

//        }

        job.join()
        println("Recognese: ${countRecognized} from ${countAll} = ${countRecognized.toDouble()/countAll*100} %")

        drawCorner(image,corners[0])
        currentCorner = 0
        updateRoutes2(textFieldVal,textFieldVal,trains,image,corners,
            date!!,0,minutes,platform1y,null,null,
            recognizedRoutes)
        textFieldVal = TextFieldValue(
            recognizedRoutes[0], TextRange(recognizedRoutes[0].length)
        )

//        currentCorner = 0
//            drawCorner(image,corners[0])

//            currentCorner = 0
//            val newRoutes = StringBuilder(routes)
//            addTrainIfNeeded(trains, image, corners, date!!, 0, minutes, platform1y, "", newRoutes, coroutineScope, scrollState, recognizedRoutes)
//            routes = newRoutes.toString()


    }



    ProgressScreen(
        image.getSubimage(0,0,image.width,image.height/4),
        trains,
        corners,{ currentCorner; },
        textFieldVal,
        goNext,{},{
            currentCorner = updateRoutes2(it, textFieldVal, trains, image, corners, date!!, 0, minutes, platform1y,null,null,recognizedRoutes)
            if (it.text == textFieldVal.text+"\n"){
                textFieldVal = TextFieldValue(it.text+recognizedRoutes[currentCorner], TextRange(it.text.length+recognizedRoutes[currentCorner].length))
            } else {
                textFieldVal = it
            }
            drawCorner(image,corners[currentCorner]);
        },
        {
            val file = File(file.parent  + "/!!!up"+ file.nameWithoutExtension + ".txt")
            train.UtilSaver<Train>(file.absolutePath).save(trains)
            returnMessage("Файл ${file.absolutePath} сохранен")
        }
    )
}