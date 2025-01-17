package OCR

import androidx.compose.ui.res.useResource
import net.sourceforge.tess4j.Tesseract
import java.io.File

fun tess(image: File,pageSegMode: Int = 8): String {
    var res = ""
    try {
        val tesseract = Tesseract()

//        useResource("/tessdata/",{ it. })
        tesseract.setDatapath("./tessdata/")
        tesseract.setLanguage("rus");

        tesseract.setPageSegMode(1)
        tesseract.setOcrEngineMode(pageSegMode)

        val s = tesseract.doOCR(image)
        res = s.removeSuffix("\n")
    } catch(_: Exception) {}
    return res
}