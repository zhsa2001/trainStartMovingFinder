package ui.screens

import ImageProcessing.BinaryColorSchemeConverter
import ImageProcessing.GrayColorSchemeConverter
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import asposeFolder
import clearFolder
import drawCorner
import folderSubimages
import getBoxes
import getHorisontalLines
import getSubArea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import train.Train
import updateRoutes2
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.PI
import kotlin.math.min

@Composable
fun UpPartProgressScreen(
    file: File,
    date: Calendar?,
    minutes: Int,
    goNext: () -> Unit,
    returnToStart: () -> Unit,
    returnMessage: (String) -> Unit,
) {
    var image by remember { mutableStateOf<BufferedImage?>(null) }

    val trains by remember { mutableStateOf(mutableListOf<Train>()) }
    var platform1y by remember { mutableStateOf(0) }
    var recognizedRoutes by remember { mutableStateOf(mutableListOf<String>()) }

    var corners by remember { mutableStateOf(mutableListOf<Point>()) }
    var currentCorner by remember { mutableStateOf(-1) }


    val coroutineScopeForSendRequest = rememberCoroutineScope()
    var isStopped by remember { mutableStateOf(false) }

    var textFieldVal by remember {
        mutableStateOf(
            TextFieldValue(
                text = ""
            )
        )
    }

    LaunchedEffect(Unit) {
        try {
            image = ImageIO.read(file)
            platform1y = image!!.height
            Thread {
                try {
                    if (!isStopped) {
                        corners = getBoxes(
                            BinaryColorSchemeConverter(170).convert(
                                GrayColorSchemeConverter().convert(image!!)
                            )
                        )
                    }
                    if (!isStopped) {
                        corners.sortBy { it.x }
                    }
                    if (!isStopped) {
                        clearFolder(folderSubimages)
                        clearFolder(asposeFolder)
                        recognizedRoutes = MutableList<String>(corners.size, { "" })
                    }

                    if (!isStopped) {
                        for (i in 0..min(corners.size, 10)) {
                            if (platform1y > corners[i].y) {
                                platform1y = corners[i].y
                            }
                        }
                    }
                    var y = getHorisontalLines(
                        BinaryColorSchemeConverter(threshold = 200).convert(
                            GrayColorSchemeConverter().convert(image!!)
                        )
                    )[2].y
                    val height = 30

                    coroutineScopeForSendRequest.launch {
                        var job = launch {
                            val dispatcher = Dispatchers.Default.limitedParallelism(3)
                            for (i in 0..<corners.size) {
                                if (isStopped) {
                                    break
                                }
                                launch(dispatcher) {
                                    recognizedRoutes[i] = (getSubArea(
                                        image!!, Point(corners[i].x, y), -PI * 58 / 180, Point(
                                            if (i + 1 < corners.size)
                                                corners[i + 1].x
                                            else
                                                corners[i].x + height, y
                                        )
                                    ))

                                }
                            }


                        }
                        job.join()
                        if (!isStopped) {
                            drawCorner(image!!, corners[0])
                            currentCorner = 0
                            updateRoutes2(
                                textFieldVal, textFieldVal, trains, image!!, corners,
                                date!!, 0, minutes, platform1y,
                                recognizedRoutes
                            )
                            textFieldVal = TextFieldValue(
                                recognizedRoutes[0], TextRange(recognizedRoutes[0].length)
                            )
                        }
                    }
                } catch (_: Exception) {
                    returnMessage("Произошла ошибка при обработке файла ${file.absolutePath}")
                    goNext()
                }

            }.start()
        } catch (_: Exception) {
            returnMessage("Произошла ошибка при обработке файла ${file.absolutePath}")
            goNext()
        }

    }


    image?.let {
        val image = image!!
        ProgressScreen(
            image.getSubimage(0, 0, image.width, image.height / 4),
            trains,
            corners, { currentCorner; },
            textFieldVal,
            goNext, {}, {
                currentCorner = updateRoutes2(
                    it,
                    textFieldVal,
                    trains,
                    image,
                    corners,
                    date!!,
                    0,
                    minutes,
                    platform1y,
                    recognizedRoutes
                )
                if (it.text == textFieldVal.text + "\n") {
                    textFieldVal = TextFieldValue(
                        it.text + recognizedRoutes[currentCorner],
                        TextRange(it.text.length + recognizedRoutes[currentCorner].length)
                    )
                } else {
                    textFieldVal = it
                }
                drawCorner(image, corners[currentCorner]);
            },
            {
                val file = File(file.parent + "/up_" + file.nameWithoutExtension + ".txt")
                train.UtilSaver<Train>(file.absolutePath).save(trains)
                returnMessage("Файл ${file.absolutePath} сохранен")
            },
            {
                isStopped = true
                returnToStart()
            }
        )
    }

}