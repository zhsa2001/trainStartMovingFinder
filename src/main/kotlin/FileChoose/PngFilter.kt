package FileChoose

import java.io.File
import javax.swing.filechooser.FileFilter


class PngFilter: FileFilter() {
    override fun accept(pathname: File?): Boolean {
        return pathname!!.extension == "png" ||
//                pathname.extension == "tif" ||
                pathname.isDirectory
    }

    override fun getDescription(): String {
        return "Png only"
    }
}