package ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import java.io.File

@Composable
fun SelectFileButton(text: String, onFileSelected: (File?)->Unit, getFileSource:()->File?){
    var file by remember { mutableStateOf<File?>(null) }
    Row {
        Button(
            onClick = {
                file = getFileSource()
                onFileSelected(file)
            }
        ) {
            Text(text)
        }
        file?.let { Text(it.name,modifier = Modifier.align(Alignment.CenterVertically),fontStyle = FontStyle.Italic) }
    }
}
