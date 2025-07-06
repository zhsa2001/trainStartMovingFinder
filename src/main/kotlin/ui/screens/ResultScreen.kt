package ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Экран с результатами
 * @param message сообщение с результатами
 * @param returnToStart возврат к началу
 */
@Composable
fun ResultScreen(message: String, returnToStart:()->Unit){
    Column {
        Column{
            Button(onClick = {
                returnToStart()
            }
            ) {
                Text("Вернуться в начало")
            }
        }
        Column{
            val state = rememberScrollState()
            Text(message, Modifier.verticalScroll(state))
        }
    }
}