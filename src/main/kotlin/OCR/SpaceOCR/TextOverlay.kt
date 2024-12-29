package OCR.SpaceOCR

import kotlinx.serialization.Serializable

@Serializable
data class TextOverlay (
    var Lines      : ArrayList<String> = arrayListOf(),
    var HasOverlay : Boolean?          = null,
    var Message    : String?           = null

)