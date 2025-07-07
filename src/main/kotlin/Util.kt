import imageprocessing.findDownCorner
import imageprocessing.findHorizontalLine
import imageprocessing.findUpCorner
import ocr.ASP
import ocr.spaceocr.SpaceOCR
import ocr.tess
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import train.SecondLineRoutesCollection
import train.TrainInfo
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

/**
 * Формирует список диапазонов присутствия маршрутов на нижней половине от Москвы-Сити, а также изменяет listOfRoutes,
 * добавляя маршруты, приходящие на нижнюю половину с Александровского сада
 * @param trains список информации об уходах маршрутов с верхней половины от Александровского сада
 * @param listOfRoutes заполняемый список с маршрутами, уходящими с Александровского сада на Москву-Сити
 * @return список диапазонов присутствия маршрутов
 */
fun formListTrainsInSecondLine(trains: List<TrainInfo>, listOfRoutes: MutableList<Int> = mutableListOf<Int>()): SecondLineRoutesCollection {
    var secondLineStart = HashMap<Int, TrainInfo>()
    var secondLineEnd = HashMap<Int, TrainInfo>()
    var secondLine = SecondLineRoutesCollection()
    for (i in trains.indices){
        if (trains[i].platform == 1){
            if(!secondLineStart.containsKey(trains[i].route)){
                secondLineStart[trains[i].route] = trains[i]
            }
            secondLineEnd[trains[i].route] = trains[i]
            if(trains[i].isGoingToDepo){
                secondLine.addDiapason(
                    trains[i].route,
                    Pair(secondLineStart.remove(trains[i].route)!!.time,
                        secondLineEnd.remove(trains[i].route)!!.time))
            } else {
                listOfRoutes.add(trains[i].route)
            }
        } else {
            if(secondLineStart.containsKey(trains[i].route)){
                secondLine.addDiapason(
                    trains[i].route,
                    Pair(secondLineStart.remove(trains[i].route)!!.time,
                        trains[i].time))
                secondLineEnd.remove(trains[i].route)
            }
        }

    }
    var routesRemains = secondLineStart.values.toMutableList()
    for(i in 0..<routesRemains.size){
        secondLine.addDiapason(
            routesRemains[i].route,
            Pair(
                secondLineStart.remove(routesRemains[i].route)!!.time,
                secondLineEnd.remove(routesRemains[i].route)!!.time
            )
        )
    }
    return secondLine
}

/**
 * Очищает директорию. Если директория не существует, то создает ее
 * @param folderSubimages директория
 */
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

/**
 * Проверяет, является ли строка числом
 */
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

/**
 * Вычисляет расстояние между точками
 * @param p1 координаты одной из точек
 * @param p2 координаты второй из точек
 * @return расстояние
 */
fun dist(p1: Point, p2: Point): Double =
    //sqrt((p1.x - p2.x).toDouble().pow(2) + (p1.y - p2.y).toDouble().pow(2))
    (Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y)).toDouble()

/**
 * Находит в области изображения список всех горизонталей, начинающихся с левой стороны области
 * @param grayImage изображение
 * @param boxWidth ширина области
 * @param boxHeight длина области
 * @return список точек, в которых начинаются горизонтали
 */
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

/**
 * Находит углы уходов поездов на изображении с отправлением от Александровского сада
 * @param grayImage изображение
 * @param boxSize сторона квадратной области
 * @return список точек-уходов
 */
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

/**
 * Находит углы уходов поездов на изображении с отправлением от Москвы-Сити
 * @param grayImage изображение
 * @param boxSize сторона квадратной области
 * @return список точек-уходов
 */
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

/**
 * Удаляет из списка точек ухода выбросы для отправления с Александровского сада
 * @param corners список точек-уходов
 * @param yUp верхняя граница точек
 * @param yBottom нижняя граница точек
 */
fun deleteNotStartTrainPoints(corners: MutableList<Point>,yUp: Int, yBottom: Int){
    var i = 0
    while(i < corners.size) {
        while(i < corners.size &&
            !(corners[i].y in yUp ..<yBottom)){
            corners.remove(corners[i])
        }
        i++
    }
}

/**
 * Удаляет из списка точек ухода выбросы для отправления с Москвы-Сити
 * @param corners список точек-уходов
 * @param yUp верхняя граница точек
 * @param yBottom нижняя граница точек
 */
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

/**
 * Вычисляет время и номер платформы для заданной точки
 * @param train информация о поезде для заполнения времени и платформы
 * @param corner точка-угол с отправлением поезда
 * @param image изображение-график
 * @param startDate время начала графика
 * @param hours длительность в часай
 * @param minutes длительность в минутах
 * @param platform1y координата y первой платформы
 */
fun setTrainTimeAndPlatformFromCorner(train: TrainInfo, corner: Point, image: BufferedImage, startDate: Calendar, hours: Int, minutes: Int, platform1y: Int){
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

/**
 * Распознает в подобласти у точки угла-ухода со станции номер маршрута.
 * Работает для верхней части, с отправлением от Александровского сада
 * @param image график
 * @param corner текущий угол-уход со станции
 * @param angle угол поворота линии, над которой должен находиться номер маршрута
 * @param nextCorner следующая точка-уход со станции
 * @return строку с распознанным маршрутом
 */
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

    val file = File("$folderSubimages","draw_${corner.x}.png")
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
            if (resTess.length in 1..2 && checkIsNum(resTess) || mayBeIs2DigitNum(resTess) || mayBeIsDigitNum(resASP)) {
                if (mayBeIs2DigitNum(resTess)) {
                    res = resTess.substring(resTess.length - 2)
                } else if (mayBeIsDigitNum(resASP)) {
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

/**
 * Преобразует изображение в черно-белое, черным выделяются красные пиксели
 * @param image изменяемое изображение
 */
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

/**
 * Проверяет, содержит ли строка двузначное число. Число должно быть в конце строки, а остальные символы должны быть не цифрами
 * @param s строка
 * @return true, если строка может содержать 2-значное число
 */
fun mayBeIs2DigitNum(s: String): Boolean {
    return s.length > 2 &&
            checkIsNum(s.substring(s.length - 2)) &&
            notContainsNumber(s.substring(0,s.length - 2))
}

/**
 * Проверяет, содержит ли строка число. Делит по пробелу, если одно из них число, то возвращает true
 * @param s строка
 * @return true, если строка может содержать число
 */
fun mayBeIsDigitNum(s: String): Boolean {
    var isContainsNum = false
    for (el in s.split(" ","\n")){
        if (el.isNotEmpty() && checkIsNum(el)){
            isContainsNum = true
            break
        }
    }
    return isContainsNum
}

/**
 * Выбирает число из строки. Делит по пробелу и рассматривает каждую подстроку как число
 * @param s строка
 * @return найденное число
 */
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

/**
 * Проверяет, что строка не содержит цифр
 * @param s проверяемая строка
 * @return true, если строка не содержит ни одной цифры
 */
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

/**
 * Выделяет квадратом заданного размера область вокруг точки-угла с отправлением поезда со станции
 * @param image изображение, на котором рисуется область
 * @param corner точка ухода для выделения
 * @param boxSize размер области
 */
fun drawCorner(image: BufferedImage, corner: Point, boxSize: Int = 30){
    val drawImage = image.createGraphics()
    drawImage.color = Color.ORANGE
    val stroke = BasicStroke(2.0f)
    drawImage.stroke = stroke
    drawImage.drawRect(corner.x-boxSize/2,corner.y-boxSize/2,boxSize,boxSize)
}

/**
 * Масштабирует изображение
 * @param imageScaled изображение для результирующего изображения
 * @param scale vfcinf,
 * @param image исходное изображение
 */
fun resizeImage(imageScaled: BufferedImage,scale: Double,image: BufferedImage){
    val graphics = imageScaled.createGraphics()
    graphics.scale(scale,scale)
    graphics.drawImage(image,0,0,null)
}

/**
 * Получает часть изображения для показа текущего фрагмента пользователю
 * @param image исходное изображение
 * @param currentCorner -текущий угол
 * @param workArea номер текущей показываемой области
 * @param parts число всех областей
 * @param padding отступ, включаемый в рабочую область
 */
fun getWorkArea(image: BufferedImage, currentCorner: Int, workArea:Int, parts: Int, padding: Int): BufferedImage {
    val partOfImage = image.getSubimage(image.width / parts * workArea, 0,
        min(image.width / parts + padding,
            image.width - image.width / parts * workArea
        ), image.height)
    return partOfImage
}

/**
 * Обновляет список с информацией об уходящиих поездах в соответствии с новым значением из поля ввода
 * @param newTextFieldVal новое значение
 * @param oldTextFieldVal старое значение
 * @param trains заполняемая информация
 * @param image изображение-график
 * @param corners список точек ухода поездов
 * @param date время начала графика
 * @param hours длительность графика в часах
 * @param minutes длительность графика в минутах
 * @param platform1y координата y первой платформы
 * @param recognisedRoutes список определенных автоматически маршрутов
 * @return номер текущего угла после обновления инфомрации
 */
fun updateRoutes2(newTextFieldVal: TextFieldValue,
                  oldTextFieldVal: TextFieldValue,
                  trains: MutableList<TrainInfo>,
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
        val train = TrainInfo()
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

/**
 * Считывает информацию о поездах, уходящих с Александровского сада
 * @param file файл с информацией
 * @param tab символ разделения времени, номера маршрута и пр. в строке
 * @return список с прочитанной информацией
 */
fun getTrainsFromFile(file: File, tab: String = "\t"): MutableList<TrainInfo> {
    val trains = mutableListOf<TrainInfo>()
    try{
        val trainLines = Files.readAllLines(file.toPath())

        for(line in trainLines){
            val data = line.split(tab)
            val train = TrainInfo()
            train.time = TrainInfo.dateTimeFormat.parse(data[0])
            train.platform = data[1].toInt()
            train.route = data[2].toInt()
            train.isGoingToDepo = data[3].toInt() == 1
            trains.add(train)
        }
    } catch(e:Exception){

    }
    return trains
}