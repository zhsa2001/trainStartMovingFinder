import ImageProcessing.findDownCorner
import ImageProcessing.findHorizontalLine
import ImageProcessing.findUpCorner
import OCR.ASP
import OCR.SpaceOCR.SpaceOCR
import OCR.tess
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import train.SecondLineRoutesCollection
import train.Train
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Image
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.min

fun formListTrainsInSecondLine(trains: List<Train>, listOfRoutes: MutableList<Int> = mutableListOf<Int>()): SecondLineRoutesCollection {
    var secondLineStart = HashMap<Int, Train>()
    var secondLineEnd = HashMap<Int, Train>()
    var secondLine = SecondLineRoutesCollection()
    for (i in trains.indices){
        if (trains[i].platform == 1){
            if(!secondLineStart.containsKey(trains[i].route)){
                secondLineStart[trains[i].route] = trains[i]
            }
            secondLineEnd[trains[i].route] = trains[i]
            if(trains[i].isGoingToDepo){
                secondLine.addDiapasone(
                    trains[i].route,
                    Pair(secondLineStart.remove(trains[i].route)!!.time,
                        secondLineEnd.remove(trains[i].route)!!.time))
            } else {
                listOfRoutes.add(trains[i].route)
            }
        } else {
            if(secondLineStart.containsKey(trains[i].route)){
                secondLine.addDiapasone(
                    trains[i].route,
                    Pair(secondLineStart.remove(trains[i].route)!!.time,
                        trains[i].time))
                secondLineEnd.remove(trains[i].route)
            }
        }

    }
    var routesRemains = secondLineStart.values.toMutableList()
    for(i in 0..<routesRemains.size){
        secondLine.addDiapasone(
            routesRemains[i].route,
            Pair(
                secondLineStart.remove(routesRemains[i].route)!!.time,
                secondLineEnd.remove(routesRemains[i].route)!!.time
            )
        )
    }
    return secondLine
}

fun clearFolder(folderSubimages: String) {
    val dir = File(folderSubimages)
    if (!dir.exists()){
        Files.createDirectory(dir.toPath())
    }
    val files = dir.listFiles()
    if (files != null) {
        for (file in files) {
            file.delete()
        }
    }
}

fun checkIsNum(num: String):Boolean{
    var res = true
    for(c in num){
        if(!c.isDigit()){
            res = false
            break
        }
    }
    return res
}

fun dist(p1: Point, p2: Point): Double =
    //sqrt((p1.x - p2.x).toDouble().pow(2) + (p1.y - p2.y).toDouble().pow(2))
    (Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y)).toDouble()

fun getHorisontalLines(grayImage: BufferedImage, boxWidth: Int = 500, boxHeight: Int = 30): MutableList<Point> {
    val horisontalLines = mutableListOf<Point>()
    var i = 4
    while(i < grayImage.height-boxHeight){
        val line = findHorizontalLine(grayImage,boxHeight,i,boxWidth, boxHeight)
        if(line.size == 2 && abs(line[0].x-line[1].x) > boxWidth - 3){
            horisontalLines.add(line[0])
            i = line[0].y
        }
        i += 5
    }
    return horisontalLines
}

fun getBoxes(grayImage: BufferedImage, boxSize: Int = 30): MutableList<Point> {
    val corners = mutableListOf<Point>()
    val lines = getHorisontalLines(grayImage)
    for(i in 0..grayImage.height/4-boxSize step 13){
        for(j in 0..grayImage.width-1-boxSize step 13){
            findUpCorner(grayImage,j,i,boxSize,corners)
        }
    }
    deleteNotStartTrainPoints(corners,lines[1].y,lines[2].y)
    return corners
}

fun getBoxes2(grayImage: BufferedImage, boxSize: Int = 30): MutableList<Point> {
    val corners = mutableListOf<Point>()
    val lines = getHorisontalLines(grayImage)
    for(i in lines[lines.size-1].y..<grayImage.height-boxSize step 10){
        for(j in 0..grayImage.width-1-boxSize step 4){
            findDownCorner(grayImage,j,i,boxSize,corners)
        }
    }
    deleteNotStartTrainPoints2(corners,lines[lines.size-1].y)
    return corners
}

fun deleteNotStartTrainPoints( corners: MutableList<Point>,yUp: Int, yBottom: Int){
    var i = 0
    while(i < corners.size) {
        while(i < corners.size &&
            !(corners[i].y in yUp ..<yBottom)){
            corners.remove(corners[i])
        }
        i++
    }
}

fun deleteNotStartTrainPoints2( corners: MutableList<Point>,yUp: Int){
    var i = 0
    while(i < corners.size) {
        while(i < corners.size &&
            !(corners[i].y > yUp)){
            corners.remove(corners[i])
        }
        i++
    }
}

fun setTrainTimeAndPlatformFromCorner(train: Train, corner: Point, image: BufferedImage, startDate: Calendar, hours: Int, minutes: Int, platform1y: Int){
    val minutes = hours*60 + minutes
    val seconds = minutes*60

    val part = (corner.x).toDouble() / image.width
    var secondsForCorner = (part*seconds).toInt()
    val trainTime = Calendar.Builder()
        .setDate(startDate[Calendar.YEAR],startDate[Calendar.MONTH],startDate[Calendar.DAY_OF_MONTH])
        .setTimeOfDay(startDate[Calendar.HOUR_OF_DAY],startDate[Calendar.MINUTE],startDate[Calendar.SECOND])
        .build()
    trainTime.add(Calendar.SECOND,secondsForCorner)
    trainTime.add(Calendar.SECOND, if (trainTime[Calendar.SECOND] % 15 < 8) -(trainTime[Calendar.SECOND]  % 15) else (15 - trainTime[Calendar.SECOND]  % 15))
    train.time = trainTime.time
    train.platform = if (Math.abs(corner.y - platform1y) < 4) 1 else 2
}

suspend fun getSubArea(image: BufferedImage, corner: Point, angle: Double, nextCorner: Point): String {
    var height = nextCorner.x - corner.x
    var width = 100
    var imageForScaling = BufferedImage(width,width + height,BufferedImage.TYPE_INT_RGB)
    var drawImageForScaling = imageForScaling.createGraphics()

    drawImageForScaling.translate(-corner.x,-corner.y + height)
    drawImageForScaling.drawImage(image,0,0,null)

    val scale = 5.0
    var imageForRotate = BufferedImage((width * scale).toInt(), (height * scale).toInt(),BufferedImage.TYPE_INT_RGB)
    val drawImageForRotate = imageForRotate.createGraphics()
    drawImageForRotate.translate(0,(height * scale).toInt())
    drawImageForRotate.rotate(angle)
    drawImageForRotate.translate(0,-(height * scale).toInt())
    drawImageForRotate.drawImage(imageForScaling.getScaledInstance((width * scale).toInt(),((width + height) * scale).toInt(), Image.SCALE_SMOOTH),0,0,null)
    var imageSmoothed = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    imageSmoothed.createGraphics().drawImage(
        imageForRotate.getScaledInstance((width).toInt(),(height).toInt(),Image.SCALE_AREA_AVERAGING),0,0,null
    )

    val file = File("$folderSubimages\\draw_${corner.x}.png")
    onlyRed(imageSmoothed)


    var res = ""
    var resASP = ASP(imageSmoothed)

    if (resASP.length in 1..2 && checkIsNum(resASP) || mayBeIs2DigitNum(resASP)){
        if (mayBeIs2DigitNum(resASP)) {
            res = resASP.substring(resASP.length - 2)
        } else {
            res = resASP
        }
    } else {
        withContext(Dispatchers.IO) {
            ImageIO.write(imageSmoothed, "PNG", file)
            var resTess = tess(file)
            if (resTess.length in 1..2 && checkIsNum(resTess) || mayBeIs2DigitNum(resTess) || mayBeIs1DigitNum(resASP)) {
                if (mayBeIs2DigitNum(resTess)) {
                    res = resTess.substring(resTess.length - 2)
                } else if (mayBeIs1DigitNum(resASP)) {
                    val num = get1DigitNum(resASP)
                    res = if (num == 0) "" else num.toString()
                } else {
                    res = resTess
                }
            } else {
                res = SpaceOCR.sendImageFor1Number(file)
                if (!(res.length in 1..2) || !checkIsNum(res)){
                    res = ""
                }
            }
        }
    }
    return res

}

fun onlyRed(image: BufferedImage){
    val raster = image.data
    var pixel = IntArray(4)
    var threshold2 = 20
    for(i in 0..<image.width){
        for(j in 0..<image.height){
            raster.getPixel(i,j,pixel)
            if (!(pixel[0] - pixel[1] > threshold2 && pixel[0] - pixel[2] > threshold2)) {
                var argb = 0
                argb += (255 as Int and 0xff) shl 24 // alpha value
                argb += (255 as Int and 0xff) // blue value
                argb += (255 as Int and 0xff) shl 8 // green value
                argb += (255 as Int and 0xff) shl 16 // red value
                image.setRGB(i,j,argb)
            }
        }
    }
}


fun mayBeIs2DigitNum(s: String): Boolean {
    return s.length > 2 &&
            checkIsNum(s.substring(s.length - 2)) &&
            notContainsNumber(s.substring(0,s.length - 2))
}

fun mayBeIs1DigitNum(s: String): Boolean {
    var isContainsNum = false
    for (el in s.split(" ","\n")){
        if (el.isNotEmpty() && checkIsNum(el)){
            isContainsNum = true
            break
        }
    }
    return isContainsNum
}

fun get1DigitNum(s: String): Int {
    var num = 0
    for (el in s.split(" ","\n")){
        if (el.isNotEmpty() && checkIsNum(el)){
            num = el.toInt()
            break
        }
    }
    return num
}

fun notContainsNumber(s: String): Boolean {
    var flag = true
    for(ch in s){
        if (ch.isDigit()){
            flag = false
            break
        }
    }
    return flag
}


fun drawCorner(image: BufferedImage, corner: Point, boxSize: Int = 30){
    val drawImage = image.createGraphics()
    drawImage.color = Color.ORANGE
    val stroke = BasicStroke(2.0f)
    drawImage.stroke = stroke
    drawImage.drawRect(corner.x-boxSize/2,corner.y-boxSize/2,boxSize,boxSize)
}

fun resizeImage(imageScaled: BufferedImage,scale: Double,image: BufferedImage){
    val graphics = imageScaled.createGraphics()
    graphics.scale(scale,scale)
    graphics.drawImage(image,0,0,null)
}

fun getWorkArea(image: BufferedImage, currentCorner: Int, workArea:Int, parts: Int, padding: Int): BufferedImage {
    val partOfImage = image.getSubimage(image.width / parts * workArea, 0,
        min(image.width / parts + padding,
            image.width - image.width / parts * workArea
        ), image.height)
    return partOfImage
}

fun updateRoutes2(newTextFieldVal: TextFieldValue,
                  oldTextFieldVal: TextFieldValue,
                  trains: MutableList<Train>,
                  image: BufferedImage,
                  corners: MutableList<Point>,
                  date: Calendar,
                  hours: Int,
                  minutes: Int,
                  platform1y: Int,
                  recognisedRoutes: MutableList<String>
                  ): Int {
    val stringRoutes = newTextFieldVal.text.split("\n")
    val currentCorner = min(stringRoutes.size-1,corners.size-1);
    if (currentCorner >= trains.size){
        val train = Train()
        setTrainTimeAndPlatformFromCorner(train, corners[currentCorner], image, date!!, hours, minutes, platform1y)
        trains.add(train)

        if(newTextFieldVal.text.startsWith(oldTextFieldVal.text)){
            train.route = if (recognisedRoutes.size > currentCorner && recognisedRoutes[currentCorner] != "") recognisedRoutes[currentCorner].toInt() else 0

        }
    }
    for(i in trains.indices){
        if(i < stringRoutes.size){
            if (checkIsNum(stringRoutes[i]) && stringRoutes[i] != "" && stringRoutes[i].length < 6)
                trains[i].route = stringRoutes[i].toInt()
        }
    }
    return currentCorner
}

fun getTrainsFromFile(file: File, tab: String = "\t"): MutableList<Train> {
    val trains = mutableListOf<Train>()
    try{
        val trainLines = Files.readAllLines(file.toPath())

        for(line in trainLines){
            val data = line.split(tab)
            val train = Train()
            train.time = train.dateTimeFormat.parse(data[0])
            train.platform = data[1].toInt()
            train.route = data[2].toInt()
            train.isGoingToDepo = data[3].toInt() == 1
            trains.add(train)
        }
    } catch(e:Exception){

    }
    return trains
}