package ui.utils

import ui.fileChoose.ExtensionFileChooser
import ui.fileChoose.PngFilter
import ui.fileChoose.TxtFilter
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

/**
 * Устанавливает PngFilter только для выбора png-изображения
 * @param path начальная директория для открытия в менеджере файлов
 * @return выбранный файл
 */
fun getImageSource(path: File): File? = getFileFromChooseDialog(PngFilter(),path)
/**
 * Устанавливает TxtFilter только для выбора txt-изображения
 * @param path начальная директория для открытия в менеджере файлов
 * @return выбранный файл
 */
fun getTxtSource(path: File): File? = getFileFromChooseDialog(TxtFilter(),path)

/**
 * Устанавливает произвольный фильтр для выбора одного файла
 * @param fileFilter фильтр для файлов
 * @param path начальная директория для открытия в менеджере файлов
 * @return выбранный файл
 */
fun getFileFromChooseDialog(fileFilter: FileFilter,path:File): File?{
    val fc = ExtensionFileChooser(fileFilter,path)
    return if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        fc.selectedFile else null
}