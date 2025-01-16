package ui.screens

import ImageProcessing.BinaryColorSchemeConverter
import ImageProcessing.GrayColorSchemeConverter
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import drawCorner
import formListTrainsInSecondLine
import getBoxes2
import train.SecondLineRoutesCollection
import train.Train
import train.UtilSaver
import updateRoutes2
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import java.util.*

@Composable
fun DownPartProgressScreen(image: BufferedImage, file: File, date: Calendar?, minutes: Int, goNext: () -> Unit, returnToStart:()->Unit, returnMessage: (String)-> Unit, trains: MutableList<train.Train>){
    var corners by remember { mutableStateOf(mutableListOf<Point>()) }
    var currentCorner by remember { mutableStateOf(-1) }

    val trains2 by remember { mutableStateOf(mutableListOf<train.Train>()) }
    var recognisedRoutes by remember { mutableStateOf(mutableListOf<Int>()) }
    var offsetForRecognisedRoutes by remember { mutableStateOf(0) }


    var textFieldVal by remember {
        mutableStateOf(
            TextFieldValue(
                text = ""
            )
        )
    }
    var secondLineRoutesCollection by remember { mutableStateOf(train.SecondLineRoutesCollection()) }

    var platform1y by remember { mutableStateOf(0) }

    LaunchedEffect(Unit){
        corners = getBoxes2(
            BinaryColorSchemeConverter(170).convert(
                GrayColorSchemeConverter().convert(image)))

        corners.sortBy {it.x}
//        for(i in 0..<min(corners.size,45)){
//            if(platform1y < corners[i].y){
//                platform1y = corners[i].y
//            }
//        }
//        var countPlayform1 = 0
//        for(i in 0..<min(10,corners.size)){
//            if(abs(platform1y - corners[i].y) < 4){
//                countPlayform1++
//            }
//        }
//        if (countPlayform1 == min(10,corners.size)){
//            platform1y = 0
//        }

        currentCorner = -1
        currentCorner = updateRoutes2(textFieldVal, textFieldVal, trains2, image, corners, date!!, 0, minutes, platform1y,null,null,MutableList<String>(corners.size,{""}))
        drawCorner(image,corners[0])

        secondLineRoutesCollection = formListTrainsInSecondLine(trains, recognisedRoutes)
//        print(secondLineRoutesCollection)

    }
    ProgressScreen(
        image.getSubimage(0,image.height*3/4,image.width,image.height/4),
        trains2,
        corners,{ currentCorner; },textFieldVal, goNext,{},{
        currentCorner = updateRoutes2(it, textFieldVal, trains2, image, corners, date!!, 0, minutes, platform1y,null,null,MutableList<String>(corners.size,{""}))
        if (it.text == textFieldVal.text+"\n"){
            if(offsetForRecognisedRoutes == 0 || offsetForRecognisedRoutes >= recognisedRoutes.size){
                textFieldVal = TextFieldValue(it.text, TextRange(it.text.length))
            } else {
                textFieldVal = TextFieldValue(it.text + recognisedRoutes[offsetForRecognisedRoutes], TextRange(it.text.length+recognisedRoutes[offsetForRecognisedRoutes].toString().length))
                trains2[currentCorner].route = recognisedRoutes[offsetForRecognisedRoutes]
                offsetForRecognisedRoutes++

            }
        } else {
            textFieldVal = it
            if(offsetForRecognisedRoutes == 0 && offsetForRecognisedRoutes < recognisedRoutes.size) {
                if (recognisedRoutes[offsetForRecognisedRoutes] == trains2[currentCorner].route) {
                    offsetForRecognisedRoutes++
                }
            }
        }
        drawCorner(image,corners[currentCorner]);
    },
        {
            val fileTrainStart = File(file.parent  + "/!!!down_"+ file.nameWithoutExtension + ".txt")
            UtilSaver<Train>(fileTrainStart.absolutePath).save(trains2)
            var fileTrainIntervals = File(file.parent  + "/!!!intervals_"+ file.nameWithoutExtension + ".txt")
            UtilSaver<SecondLineRoutesCollection>(fileTrainIntervals.absolutePath).save(listOf(secondLineRoutesCollection))
            returnMessage("Файл ${file.absolutePath} сохранен")
        })
}