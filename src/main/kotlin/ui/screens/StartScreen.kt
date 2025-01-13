package ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun StartScreen(goToUpProcessing: () -> Unit, goToDownProcessing: () -> Unit){
    Row{
        Button(
            onClick = goToUpProcessing
        ){
            Text("Обработать верх")
        }
        Button(
            onClick = goToDownProcessing
        ){
            Text("Обработать низ")
        }
    }
}
