package ocr.spaceocr

import kotlinx.serialization.Serializable

/**
 * @param Lines число строк текста
 * @param HasOverlay имеет ли наложения
 * @param Message сообщение по завершению
 */
@Serializable
data class TextOverlay (
    var Lines      : ArrayList<String> = arrayListOf(),
    var HasOverlay : Boolean?          = null,
    var Message    : String?           = null
)