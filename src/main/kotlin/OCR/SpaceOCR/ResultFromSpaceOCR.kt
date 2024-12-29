package OCR.SpaceOCR

import kotlinx.serialization.Serializable

@Serializable
data class ResultFromSpaceOCR (

    var ParsedResults                : ArrayList<ParsedResults> = arrayListOf(),
    var OCRExitCode                  : Int?                     = null,
    var IsErroredOnProcessing        : Boolean?                 = null,
    var ProcessingTimeInMilliseconds : String?                  = null,
    var SearchablePDFURL             : String?                  = null

)