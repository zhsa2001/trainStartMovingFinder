package OCR

import net.sourceforge.tess4j.Tesseract
import java.io.File

fun tess(image: File,pageSegMode: Int = 8): String {
    val tesseract = Tesseract()
    tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
    tesseract.setLanguage("rus");

    tesseract.setPageSegMode(1)
    tesseract.setOcrEngineMode(pageSegMode)

    val s = tesseract.doOCR(image)

    return s.removeSuffix("\n")
}