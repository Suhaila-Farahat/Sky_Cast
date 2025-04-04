package com.example.skycast.view.settingScreen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skycast.R
import com.example.skycast.viewModel.SettingsViewModel
import java.util.Locale

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel(), context: Context) {
    val locationMode by viewModel.locationMode.collectAsState()
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()
    val windSpeedUnit by viewModel.windSpeedUnit.collectAsState()
    val language by viewModel.language.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1E293B), Color(0xFF2D3A4A)),
                startY = 0f, endY = 1000f
            ))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Text(
                text = stringResource(id = R.string.settings),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = stringResource(id = R.string.choose_location_mode),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = locationMode == "GPS",
                    onClick = { viewModel.setLocationMode("GPS") },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF34D399))
                )
                Text(stringResource(id = R.string.use_gps), color = Color.White, modifier = Modifier.padding(start = 8.dp))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = locationMode == "Map",
                    onClick = { viewModel.setLocationMode("Map") },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF34D399))
                )
                Text(stringResource(id = R.string.choose_on_map), color = Color.White, modifier = Modifier.padding(start = 8.dp))
            }

            DropdownMenuSelection(stringResource(id = R.string.temperature_unit), listOf("Kelvin", "Celsius", "Fahrenheit"), temperatureUnit, viewModel::setTemperatureUnit)

            DropdownMenuSelection(stringResource(id = R.string.wind_speed_unit), listOf("meter/sec", "miles/hour"), windSpeedUnit, viewModel::setWindSpeedUnit)

            DropdownMenuSelection(stringResource(id = R.string.language), listOf("English", "Arabic"), language) { selectedLanguage ->
                viewModel.setLanguage(selectedLanguage)
                setLocale(context, if (selectedLanguage == "Arabic") "ar" else "en")
                val intent = Intent(context, context::class.java)
                context.startActivity(intent)
            }
        }
    }
}

@Composable
fun DropdownMenuSelection(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF35455E), shape = RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(12.dp)
        ) {
            Text(
                text = selectedOption,
                color = Color.White,
            )
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

fun setLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}