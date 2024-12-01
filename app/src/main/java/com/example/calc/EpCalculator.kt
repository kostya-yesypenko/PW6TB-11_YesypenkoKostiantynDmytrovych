package com.example.calc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.sqrt

@Stable
data class MotorData(
    var motorName: String = "",
    var efficiency: String = "",
    var powerFactor: String = "",
    var voltage: String = "",
    var count: String = "",
    var ratedPower: String = "",
    var utilizationRate: String = "",
    var reactivePowerFactor: String = "",

    var powerProduct: String = "",
    var motorCurrent: String = "",
)

@Composable
fun MotorInputForm(motorData: MotorData) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = motorData.motorName,
        onValueChange = { motorData.motorName = it },
        label = { Text("Назва мотора") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = motorData.efficiency,
        onValueChange = { motorData.efficiency = it },
        label = { Text("ККД мотора (η)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = motorData.powerFactor,
        onValueChange = { motorData.powerFactor = it },
        label = { Text("Коефіцієнт потужності (cos φ)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = motorData.voltage,
        onValueChange = { motorData.voltage = it },
        label = { Text("Напруга (U, кВ)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = motorData.count,
        onValueChange = { motorData.count = it },
        label = { Text("Кількість (шт)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = motorData.ratedPower,
        onValueChange = { motorData.ratedPower = it },
        label = { Text("Номінальна потужність (Pн, кВт)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = motorData.utilizationRate,
        onValueChange = { motorData.utilizationRate = it },
        label = { Text("Коефіцієнт використання") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = motorData.reactivePowerFactor,
        onValueChange = { motorData.reactivePowerFactor = it },
        label = { Text("tgφ (коефіцієнт реактивної потужності)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
}

@Preview
@Composable
fun EpCalculator() {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val motorList = listOf(
        MotorData("Шліфувальний верстат", "0.92", "0.9", "0.38", "4", "20", "0.15", "1.33"),
        MotorData("Свердлильний верстат", "0.92", "0.9", "0.38", "2", "14", "0.12", "1"),
        MotorData("Фугувальний верстат", "0.92", "0.9", "0.38", "4", "42", "0.15", "1.33"),
        MotorData("Циркулярна пила", "0.92", "0.9", "0.38", "1", "36", "0.3", "1.52"),
        MotorData("Прес", "0.92", "0.9", "0.38", "1", "20", "0.5", "0.75"),
        MotorData("Полірувальний верстат", "0.92", "0.9", "0.38", "1", "40", "0.2", "1"),
        MotorData("Фрезерний верстат", "0.92", "0.9", "0.38", "2", "32", "0.2", "1"),
        MotorData("Вентилятор", "0.92", "0.9", "0.38", "1", "20", "0.65", "0.75")
    )
    var systemFactor by remember { mutableStateOf("1.25") }
    var secondaryFactor by remember { mutableStateOf("0.7") }

    var totalPowerUtilized by remember { mutableStateOf(0.0) }
    var utilCoefficient by remember { mutableStateOf("") }
    var effectiveMotorCount by remember { mutableStateOf("") }
    var fullLoadCapacity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        motorList.forEach { motor ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                MotorInputForm(motorData = motor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                var nPnKvSum = 0.0
                var nPnSum = 0.0
                var nPnSquareSum = 0.0

                motorList.forEach { motor ->
                    val count = motor.count.toDouble()
                    val ratedPower = motor.ratedPower.toDouble()
                    motor.powerProduct = "${count * ratedPower}"

                    val motorCurrent = motor.powerProduct.toDouble() /
                            (sqrt(3.0) * motor.voltage.toDouble() *
                                    motor.powerFactor.toDouble() *
                                    motor.efficiency.toDouble())
                    motor.motorCurrent = motorCurrent.toString()

                    nPnKvSum += motor.powerProduct.toDouble() * motor.utilizationRate.toDouble()
                    nPnSum += motor.powerProduct.toDouble()
                    nPnSquareSum += count * ratedPower * ratedPower
                }

                totalPowerUtilized = nPnKvSum
                utilCoefficient = (nPnKvSum / nPnSum).toString()
                effectiveMotorCount = ceil((nPnSum * nPnSum) / nPnSquareSum).toString()

                val systemMultiplier = systemFactor.toDouble()
                val realLoad = systemMultiplier * nPnKvSum
                fullLoadCapacity = realLoad.toString()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Обчислити")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Коефіцієнт використання: $utilCoefficient", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Ефективна кількість моторів: $effectiveMotorCount", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Повне навантаження: $fullLoadCapacity", style = MaterialTheme.typography.bodyLarge)
    }
}
