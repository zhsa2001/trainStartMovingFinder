package ui.screens

import train.Train
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import getWorkArea
import kotlinx.coroutines.launch
import resizeImage
import java.awt.Point
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Composable
fun ProgressScreen(image: BufferedImage, trains: MutableList<Train>,
                   corners: MutableList<Point>, currentCorner:  ()->Int,
                   textFieldVal: TextFieldValue,
                   goNext: () -> Unit,
                   returnToStart:()->Unit,
                   onRouteTextFieldUpdate: (TextFieldValue)->Unit,
                   save: ()->Unit) {
    val scale = 2.0
    val imageScaled = BufferedImage((image.width * scale).toInt(), (image.height * scale).toInt(), BufferedImage.TYPE_INT_RGB)

    var workArea by remember { mutableStateOf(0)}
    val parts = image.width / 1500

    val scrollState = rememberScrollState()

    val coroutineScope = rememberCoroutineScope()

    val modifierRouteTextField = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState)
    val padding = 20

    Column{
        resizeImage(imageScaled, scale, image)
        Image(getWorkArea(imageScaled,currentCorner(),workArea,parts,padding).toComposeImageBitmap(),"Область графика с началом движения")

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

                    println(Date())
                    println(scrollState.value)
                    println(1)
                    var prevCursorPositionEnd = it.selection.end == it.text.length
                    println(scrollState.value)
                    println(2)
                    onRouteTextFieldUpdate(it)
                    println(scrollState.value)
                    println(3)
                    if (prevCursorPositionEnd) {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(scrollState.maxValue+100)
                        }

                        workArea = corners[currentCorner()].x * parts / image.width
                    }
                    println(scrollState.value)
                    println(4)
                },
                modifier = modifierRouteTextField.padding(0.dp,0.dp,12.dp,0.dp),
            )
            TextField(trains.joinToString("\n"),
                onValueChange = {},
                enabled = false,
                modifier = modifierRouteTextField
            )

        }

        Button(onClick = {
            save()
            goNext()
        }){
            Text("Сохранить")
        }
    }
}