import java.io.File
import java.io.FileOutputStream

class TrainSaver(filename: String) {
    val file = File(filename)
    fun save(trains: List<Train>){
        val out = FileOutputStream(file)
        for(i in trains.indices){
            var buffer = trains[i].info() + if (i != trains.size - 1) "\n" else ""
            out.write(buffer.toByteArray());
        }
        out.close()
    }
}