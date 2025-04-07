package com.example.skycast.view.homeScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.R
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.utils.LanguageUtils
import com.example.skycast.utils.WeatherIconUtil
import com.example.skycast.utils.convertTemperature
import com.example.skycast.utils.convertWindSpeed
import com.example.skycast.utils.getFormattedDate
import com.example.skycast.utils.getFormattedTime
import com.example.skycast.viewModel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherSection(
    weather: WeatherResponse,
    settingsViewModel: SettingsViewModel,
    languageUtils: LanguageUtils
) {
    val language = languageUtils.getSavedLanguage()

    val currentTime = remember { getFormattedTime() }
    val currentDate = remember { getFormattedDate() }

    val temperatureUnit by settingsViewModel.temperatureUnit.collectAsState()
    val windSpeedUnit by settingsViewModel.windSpeedUnit.collectAsState()

    val tempUnitSymbol = when (temperatureUnit) {
        "Fahrenheit" -> "°F"
        "Kelvin" -> "K"
        else -> "°C"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = weather.name,
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "$currentDate || $currentTime",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                ?: stringResource(id = R.string.no_description),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = WeatherIconUtil.getWeatherIcon(weather.weather.firstOrNull()?.icon ?: "01d")),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(160.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        WeatherStatsCard(weather, settingsViewModel, temperatureUnit, windSpeedUnit)
    }
}

@Composable
fun WeatherStatsCard(
    weather: WeatherResponse,
    settingsViewModel: SettingsViewModel,
    temperatureUnit: String,
    windSpeedUnit: String
) {
    val tempValue = convertTemperature(weather.main.temp, "celsius", temperatureUnit).roundToInt()

    val windSpeed = convertWindSpeed(weather.wind.speed, "kmh", windSpeedUnit).roundToInt()

    val tempUnitSymbol = when (temperatureUnit) {
        "Fahrenheit" -> "°F"
        "Kelvin" -> "K"
        else -> "°C"
    }

    // Correctly handle wind speed units
    val windSpeedUnitSymbol = when (windSpeedUnit) {
        "kmh" -> "km/h"
        "mph" -> "mph"
        else -> "m/s"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetail(value = "$tempValue$tempUnitSymbol", label = stringResource(id = R.string.temperature))
                WeatherDetail(value = "$windSpeed $windSpeedUnitSymbol", label = stringResource(id = R.string.wind))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetail(value = "${weather.main.humidity}%", label = stringResource(id = R.string.humidity_label))
                WeatherDetail(value = "${weather.main.pressure}hPa", label = stringResource(id = R.string.pressure_label))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                WeatherDetail(value = "${weather.clouds.Clouds}%", label = stringResource(id = R.string.clouds))
            }
        }
    }
}

@Composable
fun WeatherDetail(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

