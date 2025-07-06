package ocr.spaceocr

import kotlinx.serialization.Serializable

/**
 * Для десериализации ответа от SpaceOCR
 * @param ParsedResults список расперсенных данных для отправленных изображений
 * @param OCRExitCode код завершения
 * @param IsErroredOnProcessing флаг возникновения ошибок
 * @param ProcessingTimeInMilliseconds время выполнения обработки
 * @param SearchablePDFURL искомый URL PDF-файла
 */
@Serializable
data class ResultFromSpaceOCR(
    var ParsedResults: ArrayList<ParsedResults> = arrayListOf(),
    var OCRExitCode: Int? = null,
    var IsErroredOnProcessing: Boolean? = null,
    var ProcessingTimeInMilliseconds: String? = null,
    var SearchablePDFURL: String? = null

)