package OCR

import com.aspose.ocr.*
import java.awt.image.BufferedImage
import java.util.ArrayList

fun ASP(image: BufferedImage): String{
    val api: AsposeOCR = AsposeOCR()
    val filters: PreprocessingFilter = PreprocessingFilter()
    filters.add(PreprocessingFilter.Scale(4F))
    filters.add(PreprocessingFilter.ToGrayscale())
    filters.add(PreprocessingFilter.BinarizeAndDilate())
    filters.add(PreprocessingFilter.Median())
    val input: OcrInput = OcrInput(InputType.SingleImage, filters)
    input.add(image)

    val result: ArrayList<RecognitionResult> = api.Recognize(input)
//    System.out.println("Aspose:${result[0].recognitionText}\\n")
    return result[0].recognitionText.removeSuffix("\n")
}