package ocr.spaceocr

import kotlinx.serialization.Serializable

/**
 * Описание найденного результата на изображении.
 * @property TextOverlay Наложение текста на изображении (не используется)
 * @property TextOrientation направление текста
 * @property FileParseExitCode код завершения парсинга файла
 * @property ParsedText распозанный текст
 * @property ErrorMessage сообщение об ошибке
 * @property ErrorDetails детали сообщения об ошибке
 * */
@Serializable
data class ParsedResults(
    var TextOverlay: TextOverlay? = TextOverlay(),
    var TextOrientation: String? = null,
    var FileParseExitCode: Int? = null,
    var ParsedText: String? = null,
    var ErrorMessage: String? = null,
    var ErrorDetails: String? = null

)