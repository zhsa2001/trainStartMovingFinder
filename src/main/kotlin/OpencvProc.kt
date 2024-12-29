import nu.pattern.OpenCV
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc


fun workWithOpenCV(name: String,resultName: String): String{
    OpenCV.loadShared()
    var imageToProcess = loadImage(name)
    Imgproc.resize(imageToProcess,imageToProcess, Size(),3.0,3.0,Imgproc.INTER_CUBIC)
    Imgproc.dilate(imageToProcess,imageToProcess,Mat.ones(3,3,CvType.CV_8U))
    Imgproc.erode(imageToProcess,imageToProcess,Mat.ones(3,3,CvType.CV_8U))
//    Imgproc.blur(imageToProcess,imageToProcess,Size(3.0,3.0))
    saveImage(imageToProcess,resultName)
    return resultName
}

fun loadImage(imagePath: String?): Mat {
    return Imgcodecs.imread(imagePath)
}

fun saveImage(imageMatrix: Mat, targetPath: String) {
    Imgcodecs.imwrite(targetPath, imageMatrix)
}