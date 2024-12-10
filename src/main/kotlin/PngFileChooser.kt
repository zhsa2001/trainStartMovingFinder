import javax.swing.JFileChooser

class PngFileChooser: JFileChooser() {
    init {
        this.setAcceptAllFileFilterUsed(false)
        this.isMultiSelectionEnabled = false
        this.addChoosableFileFilter(PngFilter())
    }
}