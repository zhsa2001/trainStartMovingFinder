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
//import drawCorner
//import formListTrainsInSecondLine
//import getBoxes2
//import getWorkArea
//import resizeImage
//import train.SecondLineRoutesCollection
//import train.Train
//import train.TrainSaver
//import updateRoutes2
//import java.awt.Point
//import java.awt.image.BufferedImage
//import java.io.File
//import java.util.*
//import kotlin.math.abs
//import kotlin.math.max
//import kotlin.math.min
//
//@Composable
//fun ProgressRouteDownScreen(image: BufferedImage, file: File, date: Calendar?, minutes: Int, goNext: () -> Unit, returnToStart:()->Unit, trains: MutableList<Train>) {
//
//    var corners by remember { mutableStateOf(mutableListOf<Point>()) }
//    var currentCorner by remember { mutableStateOf(-1) }
//    val scale = 2.0
//    val imageScaled = BufferedImage((image.width * scale).toInt(), (image.height/4 * scale).toInt(), BufferedImage.TYPE_INT_RGB)
//    var workArea by remember { mutableStateOf(0) }
//    val parts = image.width / 1500
//    val coroutineScope = rememberCoroutineScope()
//    val scrollState = rememberScrollState()
//    val padding = 20
//    val trains2 by remember { mutableStateOf(mutableListOf<Train>()) }
//    var recognisedRoutes by remember { mutableStateOf(mutableListOf<Int>()) }
//    var offsetForRecognisedRoutes by remember { mutableStateOf(0) }
//
//    var textFieldVal by remember {
//        mutableStateOf(
//            TextFieldValue(
//                text = ""
//            )
//        )
//    }
//    var secondLineRoutesCollection by remember { mutableStateOf(SecondLineRoutesCollection()) }
//
//    var platform1y by remember { mutableStateOf(0) }
//
//    LaunchedEffect(Unit){
//        corners = getBoxes2(
//            BinaryColorSchemeConverter(170).convert(
//                GrayColorSchemeConverter().convert(image)))
//
//        corners.sortBy {it.x}
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
//        println(countPlayform1)
//        if (countPlayform1 == min(10,corners.size)){
//            platform1y = 0
//        }
//
//        currentCorner = -1
//        currentCorner = updateRoutes2(textFieldVal, textFieldVal, trains2, image, corners, date!!, 0, minutes, platform1y,coroutineScope,scrollState,MutableList<String>(corners.size,{""}))
//        drawCorner(image,corners[0])
//
//        secondLineRoutesCollection = formListTrainsInSecondLine(trains, recognisedRoutes)
////        drawCorner(image,corners[0])
//
//    }
//
//    Column{
//        resizeImage(imageScaled, scale, image.getSubimage(0,image.height*3/4,image.width,image.height/4))
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
//
//        Row{
//
//            TextField(
//                value = textFieldVal, onValueChange = {
////                state.setTextAndPlaceCursorAtEnd(it)
////                    currentCorner = addTrainIfNeeded2(trains2,image, corners, date!!, 0, minutes)
//                    var prevCursorPosition = textFieldVal.selection.end
//                    currentCorner = updateRoutes2(it, textFieldVal, trains2, image, corners, date!!, 0, minutes, platform1y,coroutineScope,scrollState,MutableList<String>(corners.size,{""}))
//                    if (it.text == textFieldVal.text+"\n"){
//                        if(offsetForRecognisedRoutes == 0 || offsetForRecognisedRoutes >= recognisedRoutes.size){
//                            textFieldVal = TextFieldValue(it.text, TextRange(it.text.length))
//                        } else {
//                            textFieldVal = TextFieldValue(it.text + recognisedRoutes[offsetForRecognisedRoutes], TextRange(it.text.length+recognisedRoutes[offsetForRecognisedRoutes].toString().length))
//                            trains2[currentCorner].route = recognisedRoutes[offsetForRecognisedRoutes]
//                            offsetForRecognisedRoutes++
//
//                        }
//                    } else {
//                        textFieldVal = it
//                        if(offsetForRecognisedRoutes == 0 && offsetForRecognisedRoutes < recognisedRoutes.size) {
//                            if (recognisedRoutes[offsetForRecognisedRoutes] == trains2[currentCorner].route) {
//                                offsetForRecognisedRoutes++
//                            }
//                        }
//                    }
////                    println(textFieldVal.selection.end)
//                    drawCorner(image,corners[currentCorner]);
//                    if (prevCursorPosition == textFieldVal.text.length - 1) {
//                        workArea = corners[currentCorner].x * parts / image.width
//                    }
//                },
//                modifier = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState).padding(0.dp,0.dp,12.dp,0.dp),
//            )
//
//
////            var state = rememberTextFieldState()
////            TextField(
////            )
//            TextField(trains2.joinToString("\n"),
//                onValueChange = {},
//                enabled = false,
//                modifier = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState)
//            )
//
//        }
//
//
//
//        Button(onClick = {
//            TrainSaver(file.parent  + "/down"+ file.nameWithoutExtension + ".txt").save(trains2)
//            goNext()
//        }){
//            Text("Сохранить низ")
//        }
//
//
//    }
//}
//
