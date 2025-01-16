package ui.utils

import ui.fileChoose.ExtensionFileChooser
import ui.fileChoose.PngFilter
import ui.fileChoose.TxtFilter
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

fun getImageSource(path: File): File? = getFileFromChooseDialog(PngFilter(),path)
fun getTxtSource(path: File): File? = getFileFromChooseDialog(TxtFilter(),path)

fun getFileFromChooseDialog(fileFilter: FileFilter,path:File): File?{
    val fc = ExtensionFileChooser(fileFilter,path)
    return if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        fc.selectedFile else null
}