package OCR.SpaceOCR

import kotlinx.serialization.Serializable

@Serializable
data class ParsedResults (
    var TextOverlay       : TextOverlay? = TextOverlay(),
    var TextOrientation   : String?      = null,
    var FileParseExitCode : Int?         = null,
    var ParsedText        : String?      = null,
    var ErrorMessage      : String?      = null,
    var ErrorDetails      : String?      = null

)