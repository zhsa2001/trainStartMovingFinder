package ui.fileChoose

import java.io.File
import javax.swing.filechooser.FileFilter


/**
 * Фильтр для выбираемых png-файлов
 */
class PngFilter: FileFilter() {
    override fun accept(pathname: File?): Boolean {
        return pathname!!.extension == "png" ||
                pathname.isDirectory
    }

    override fun getDescription(): String {
        return "Png only"
    }
}