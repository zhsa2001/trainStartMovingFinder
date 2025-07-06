package ui.fileChoose

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

/**
 * Вызывает менеджер файлов для выбора файла определенного формата
 * @param fileFilter определяет формат (форматы) выбираемых файлов
 * @param path открываемая директория в менеджере файлов
 */
class ExtensionFileChooser(fileFilter: FileFilter,path: File): JFileChooser(path) {
    init {
        this.setAcceptAllFileFilterUsed(false)
        this.isMultiSelectionEnabled = false
        this.addChoosableFileFilter(fileFilter)
    }
}