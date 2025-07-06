package ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
/**
 * Начальный экран
 * @param goToUpProcessing переход к работе с верхней половиной графика
 * (отправления с Александровского сада)
 * @param goToDownProcessing переход к работе с нижней половиной графика
 * (отправления с Москвы-сити)
 */
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
