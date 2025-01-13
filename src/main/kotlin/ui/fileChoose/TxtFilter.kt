package ui.fileChoose

import java.io.File
import javax.swing.filechooser.FileFilter

class TxtFilter: FileFilter() {
    override fun accept(pathname: File?): Boolean {
        return pathname!!.extension == "txt" ||
//                pathname.extension == "tif" ||
                pathname.isDirectory
    }

    override fun getDescription(): String {
        return "Txt only"
    }
}