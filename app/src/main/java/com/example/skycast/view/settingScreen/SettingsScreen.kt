package com.example.skycast.view.settingScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skycast.viewModel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val locationMode by viewModel.locationMode.collectAsState()
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()
    val windSpeedUnit by viewModel.windSpeedUnit.collectAsState()
    val language by viewModel.language.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Settings", fontSize = 24.sp, style = MaterialTheme.typography.headlineMedium)

        // Location Mode Selection
        Text(text = "Choose Location Mode", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = locationMode == "GPS", onClick = { viewModel.setLocationMode("GPS") })
            Text("Use GPS", modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = locationMode == "Map", onClick = { viewModel.setLocationMode("Map") })
            Text("Choose on Map", modifier = Modifier.padding(start = 8.dp))
        }

        DropdownMenuSelection("Temperature Unit", listOf("Kelvin", "Celsius", "Fahrenheit"), temperatureUnit, viewModel::setTemperatureUnit)

        DropdownMenuSelection("Wind Speed Unit", listOf("meter/sec", "miles/hour"), windSpeedUnit, viewModel::setWindSpeedUnit)

        DropdownMenuSelection("Language", listOf("English", "Arabic"), language, viewModel::setLanguage)
    }
}

@Composable
fun DropdownMenuSelection(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text(text = label, fontSize = 18.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(12.dp)
        ) {
            Text(text = selectedOption)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}
