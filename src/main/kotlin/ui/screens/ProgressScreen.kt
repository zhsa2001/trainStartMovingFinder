package ui.screens

import androidx.compose.foundation.*
import train.Train
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
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
                   save: ()->Unit,
                   onStopRequest:()->Unit) {
    val scale = 2.0
    val imageScaled = BufferedImage((image.width * scale).toInt(), (image.height * scale).toInt(), BufferedImage.TYPE_INT_RGB)

    var workArea by remember { mutableStateOf(0)}
    val parts = image.width / 1500

    val scrollState = rememberScrollState()

    val coroutineScope = rememberCoroutineScope()

    val modifierRouteTextField = Modifier.fillMaxHeight(0.9f).verticalScroll(scrollState)
    val padding = 20

    val focusRequester = remember { FocusRequester() }

    Column{
        Button(onClick = {
            onStopRequest()
        }){
            Text("Отменить")
        }
        resizeImage(imageScaled, scale, image)
        Image(getWorkArea(imageScaled,currentCorner(),workArea,parts,padding).toComposeImageBitmap(),"Область графика с началом движения")

        Row {
            Button(onClick = {
                workArea = max(workArea - 1,0)
                focusRequester.requestFocus()

            }){
                Text("<<")
            }
            Button(onClick = {
                workArea = min(workArea + 1,parts-1)
                focusRequester.requestFocus()
            }){
                Text(">>")
            }
        }
        Row{
    /*
    * Column{
    *     Row{
    *       TextField маршрут
    *       TextField поезд
    * }
    * }
    * для updateRoutes2 берем массив
    * */
            TextField(
                value = textFieldVal, onValueChange = {
                    var prevCursorPositionEnd = it.selection.end == it.text.length
                    onRouteTextFieldUpdate(it)
                    if (prevCursorPositionEnd) {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(scrollState.maxValue+100)
                        }

                        workArea = corners[currentCorner()].x * parts / image.width
                    }
                },

                modifier = modifierRouteTextField
                    .padding(0.dp,0.dp,12.dp,0.dp)
                    .focusRequester(focusRequester)
                ,
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