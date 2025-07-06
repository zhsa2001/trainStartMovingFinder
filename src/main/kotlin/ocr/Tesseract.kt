package ocr

import net.sourceforge.tess4j.Tesseract
import java.io.File

/**
 * Выполняет распознавание символов с помощью Tesseract
 * @param image распознаваемое изображения
 * @return первую строку с распознанным текстом
 */
fun tess(image: File,pageSegMode: Int = 8): String {
    var res = ""
    try {
        val tesseract = Tesseract()
        tesseract.setDatapath("./tessdata/")
        tesseract.setLanguage("rus");

        tesseract.setPageSegMode(1)
        tesseract.setOcrEngineMode(pageSegMode)

        val s = tesseract.doOCR(image)
        res = s.removeSuffix("\n")
    } catch(_: Exception) {}
    return res
}