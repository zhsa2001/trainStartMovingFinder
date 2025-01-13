package ui.fileChoose

import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

class ExtensionFileChooser(fileFilter: FileFilter): JFileChooser() {
    init {
        this.setAcceptAllFileFilterUsed(false)
        this.isMultiSelectionEnabled = false
        this.addChoosableFileFilter(fileFilter)
    }
}