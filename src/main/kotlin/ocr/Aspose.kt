package ocr

import com.aspose.ocr.*
import java.awt.image.BufferedImage
import java.util.ArrayList

/**
 * Выполняет распознавание символов с помощью AsposeOCR
 * @param image распознаваемое изображения
 * @return первую строку с распознанным текстом
 */
fun ASP(image: BufferedImage): String{
    var res = ""
    try {
        val api: AsposeOCR = AsposeOCR()
        val filters: PreprocessingFilter = PreprocessingFilter()
        filters.add(PreprocessingFilter.Scale(4F))
        filters.add(PreprocessingFilter.ToGrayscale())
        filters.add(PreprocessingFilter.BinarizeAndDilate())
        filters.add(PreprocessingFilter.Median())
        val input: OcrInput = OcrInput(InputType.SingleImage, filters)
        input.add(image)

        val result: ArrayList<RecognitionResult> = api.Recognize(input)
        res = result[0].recognitionText.removeSuffix("\n")
    } catch (_: Exception){

    }
    return res
}