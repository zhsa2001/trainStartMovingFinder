package ocr.spaceocr

import kotlinx.serialization.Serializable

/**
 * Для десериализации ответа от SpaceOCR
 * @param
 */
@Serializable
data class ResultFromSpaceOCR (
    var ParsedResults                : ArrayList<ParsedResults> = arrayListOf(),
    var OCRExitCode                  : Int?                     = null,
    var IsErroredOnProcessing        : Boolean?                 = null,
    var ProcessingTimeInMilliseconds : String?                  = null,
    var SearchablePDFURL             : String?                  = null

)