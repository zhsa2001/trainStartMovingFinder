package ui.utils

import ui.fileChoose.ExtensionFileChooser
import ui.fileChoose.PngFilter
import ui.fileChoose.TxtFilter
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

fun getImageSource(): File? = getFileFromChooseDialog(PngFilter())
fun getTxtSource(): File? = getFileFromChooseDialog(TxtFilter())

fun getFileFromChooseDialog(fileFilter: FileFilter): File?{
    val fc = ExtensionFileChooser(fileFilter)
    return if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        fc.selectedFile else null
}