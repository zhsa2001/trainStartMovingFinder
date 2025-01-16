package ui.fileChoose

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

class ExtensionFileChooser(fileFilter: FileFilter,path: File): JFileChooser(path) {
    init {
        this.setAcceptAllFileFilterUsed(false)
        this.isMultiSelectionEnabled = false
        this.addChoosableFileFilter(fileFilter)
    }
}