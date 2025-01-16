package train

import java.io.File
import java.io.FileOutputStream

class UtilSaver<T: SavebleInfo>(filename: String) {
    val file = File(filename)
    fun save(elements: List<T>){
        val out = FileOutputStream(file)
        for(i in elements.indices){
            var buffer = elements[i].info() + if (i != elements.size - 1) "\n" else ""
            out.write(buffer.toByteArray());
        }
        out.close()
    }
}