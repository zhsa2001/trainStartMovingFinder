package ImageProcessing

import dist
import java.awt.Point
import java.awt.image.BufferedImage
import java.lang.Math.abs

fun findHorizontalLine(image: BufferedImage, x: Int, y: Int, boxWidth: Int, boxHeight: Int = 0): List<Point>{
    val boxHeight = if (boxHeight == 0) boxWidth else boxHeight
    var lineStart: Point? = null
    var lineEnd: Point? = null
    val raster = image.raster
    var pixel = IntArray(4)
    for(i in 0..<boxHeight){
        raster.getPixel(x,y+i,pixel)
        if(pixel[0] == 0){
            lineStart = Point(x,y+i)
            for(j in 0..<boxWidth){
                raster.getPixel(x+j,y+i,pixel)
                if(pixel[0] != 0){
                    lineEnd = Point(x+j-1,y+i)
                    break
                }
            }
            if(lineEnd == null){
                lineEnd = Point(x+boxWidth-1,y+i)
            }
        }
        if(lineEnd != null){
            break
        }
    }
    val line = mutableListOf<Point>()
    if(lineEnd != null){
        line.add(lineStart!!)
        line.add(lineEnd!!)
    }
    return line
}

fun findVerticalLine(image: BufferedImage,x: Int,y: Int,square_size: Int): List<Point>{
    var lineStart: Point? = null
    var lineEnd: Point? = null
    val raster = image.raster
    var pixel = IntArray(4)
    for(j in 0..<square_size){
        raster.getPixel(x+j,y,pixel)
        if(pixel[0] == 0){
            lineStart = Point(x+j,y)
            for(i in 0..<square_size){
                raster.getPixel(x+j,y+i,pixel)
                if(pixel[0] != 0){
                    lineEnd = Point(x+j,y+i-1)
                    break
                }
            }
            if(lineEnd == null){
                lineEnd = Point(x+j,y+square_size-1)
            }
        }
        if(lineEnd != null){
            break
        }
    }
    val line = mutableListOf<Point>()
    if(lineEnd != null){
        line.add(lineStart!!)
        line.add(lineEnd!!)
    }
    return line
}

fun findUpCorner(image: BufferedImage,x: Int, y: Int, boxSize: Int, corners: MutableList<Point>){
    var find = false
    var lineBegin = Point()
    var lineEnd = Point()
    val lh = mutableListOf<List<Point>>() // line horizontal
    val lv = mutableListOf<List<Point>>() /// line vertical
    var i = y
    val data = image.raster
    val pixel = IntArray(4)
    while (i < y + boxSize) {
        data.getPixel(x,i,pixel)
        while (i < y + boxSize && pixel[0] == 0) {
            find = true
            lineBegin = Point(x,i)
            lineEnd = lineBegin

            for(j in x..<x + boxSize){
                data.getPixel(j,i,pixel)
                if(pixel[0] != 0)
                    break
                lineEnd = Point(j,i)
            }
//            while (j < x + boxSize && pixel[0] == 0) {
//                lineEnd = Point(j,i)
//                data.getPixel(j,i,pixel)
//                j++
//            }
//            lh.add(listOf(lineBegin, lineEnd))
            i++
            data.getPixel(x,i,pixel)
        }
        if (find){
            lh.add(listOf(lineBegin, lineEnd))
//            println("$lineBegin $lineEnd ${lh.size}")
//            readln()
            find = false
//            break
        }
        i++

    }

    var j = x

    while (j < x + boxSize) {
        data.getPixel(j,y+boxSize-1,pixel)
        while (j < x + boxSize && pixel[0] == 0) {
            find = true
            i = y + boxSize - 1
            lineBegin = Point(j,i)
            lineEnd = lineBegin
            data.getPixel(j,i,pixel)
            while (i > y) {
                i--
                data.getPixel(j,i,pixel)
                if (pixel[0] != 0)
                    break
                lineEnd = Point(j,i)

            }

            j++
            data.getPixel(j,y+boxSize-1,pixel)
        }
        if (find){
            lv.add(listOf(lineBegin, lineEnd))
//            println("$lineBegin $lineEnd")
//            readln()
            find = false
        }
        j++
    }
    for (h in lh) {
        for (v in lv) {
            if (dist(h[1], v[1]) < 4
                && ((abs(h[1].x - h[0].x) > (boxSize/3) || abs(v[1].y - v[0].y) > (boxSize/3))
                        && abs(h[1].x - h[0].x) < (boxSize/10*9)
                        && abs(h[1].x - h[0].x) > (boxSize/4)
//                        && abs(v[1].y - v[0].y) > 3
                        && abs(v[1].y - v[0].y) < (boxSize/10*9))) {
                if (!corners.contains(h[1])) {
                    corners.add(h[1])
                }
                return
            }
        }
    }
}


fun findDownCorner(image: BufferedImage,x: Int, y: Int, boxSize: Int, corners: MutableList<Point>){
    var find = false
    var lineBegin = Point()
    var lineEnd = Point()
    val lh = mutableListOf<List<Point>>() // line horizontal
    val lv = mutableListOf<List<Point>>() /// line vertical
    var i = y + boxSize - 1
    val data = image.raster
    val pixel = IntArray(4)
    while (i > y) {
        data.getPixel(x,i,pixel)
        while (i < y + boxSize && pixel[0] == 0) {
            find = true
            lineBegin = Point(x,i)
            lineEnd = lineBegin

            for(j in x..<x + boxSize){
                data.getPixel(j,i,pixel)
                if(pixel[0] != 0)
                    break
                lineEnd = Point(j,i)
            }
//            while (j < x + boxSize && pixel[0] == 0) {
//                lineEnd = Point(j,i)
//                data.getPixel(j,i,pixel)
//                j++
//            }
//            lh.add(listOf(lineBegin, lineEnd))
            i--
            data.getPixel(x,i,pixel)
        }
        if (find){
            lh.add(listOf(lineBegin, lineEnd))
//            println("$lineBegin $lineEnd ${lh.size}")
//            readln()
            find = false
//            break
        }
        i--

    }

    var j = x

    while (j < x + boxSize) {
        data.getPixel(j,y,pixel)
        while (j < x + boxSize && pixel[0] == 0) {
            find = true
            i = y
            lineBegin = Point(j,i)
            lineEnd = lineBegin
            data.getPixel(j,i,pixel)
            while (i < y + boxSize - 1) {
                i++
                data.getPixel(j,i,pixel)
                if (pixel[0] != 0)
                    break
                lineEnd = Point(j,i)

            }

            j++
            data.getPixel(j,y,pixel)
        }
        if (find){
            lv.add(listOf(lineBegin, lineEnd))
//            println("$lineBegin $lineEnd")
//            readln()
            find = false
        }
        j++
    }
    for (h in lh) {
        for (v in lv) {
            if (abs(h[1].x - h[0].x) > (boxSize/2) && abs(v[1].y - v[0].y) > (boxSize/4)
                        && abs(h[1].x - h[0].x) < (boxSize/10*9)) {
                if (!corners.contains(h[1]))
                    corners.add(h[1])
                return
            }
        }
    }
}

