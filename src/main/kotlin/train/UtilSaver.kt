package train

import java.io.File
import java.io.FileOutputStream

/**
 * Для сохранения объектов класса, реализующих интерфейс SavebleInfo
 * @param filename название файла для сохранения
 * @property file файл для запаси
 */
class UtilSaver<T: SavebleInfo>(filename: String) {
    val file = File(filename)

    /**
     * Выполняет сохранение в файл file списка элементов SavebleInfo
     * @param elements список элементов
     */
    fun save(elements: List<T>){
        val out = FileOutputStream(file)
        for(i in elements.indices){
            var buffer = elements[i].info() + if (i != elements.size - 1) "\n" else ""
            out.write(buffer.toByteArray());
        }
        out.close()
    }
}