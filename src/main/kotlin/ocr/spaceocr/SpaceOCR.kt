package ocr.spaceocr

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Объект для распознавания символов с помощью SpaceOCR
 * @property url URL ресурса SpaceOCR
 * @property keys набор ключей для использования, чередуются, чтобы
 * преодолеть предельно допустимое количество запросов в час
 * @property header для отправки ключа в теле HTTP-запроса
 * @property numOfApi номер текущего ключа
 * @property client клиент для отправки HTTP-запросов
 */
object SpaceOCR{
    var url: String = "https://api.ocr.space/parse/image"
    val keys = listOf("K8938868758895","K81987102288957")
    const val header = "apikey"
    var numOfApi = 0

    val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
        }
    }

    /**
     * Получает первую линию до переноса строки из ответа, присланного от ресурса после распознавания изображения
     * @param file изображение для распознавания
     * @return строку - первую линию до переноса строки из ответа
     */
    suspend fun sendImageFor1Number(file: File): String {
        return sendImage(file).split("\r\n")[0]
    }

    /**
     * Получает ответ от ресурса после распознавания изображения
     * @param file изображение для распознавания
     * @return строку - первую линию до переноса строки из ответа
     */
    suspend fun sendImage(file: File): String {
        var res = ""
        val api = keys[numOfApi++]
        numOfApi %= keys.size
        withContext(Dispatchers.IO) {
            try {
                val request = client.request(url) {
                    method = HttpMethod.Post
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append("file", file.readBytes(), Headers.build {
                                    append(HttpHeaders.ContentType, "image/png")
                                    append(HttpHeaders.ContentDisposition, "filename=\"snimok.jpg\"")
                                })
                            },
                            boundary = "WebAppBoundary"
                        )
                    )
                    headers.append(header,api)
                }
                res = request.bodyAsText()
                var resultInJSON = Json.decodeFromString<ResultFromSpaceOCR>(res)
                res = resultInJSON.ParsedResults[0].ParsedText!!
            } catch (e: Exception){

            }
        }
        return res
    }
}
