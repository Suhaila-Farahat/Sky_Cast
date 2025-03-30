package com.example.skycast.view.homeScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.utils.WeatherIconUtil
import com.example.skycast.utils.convertTemperature
import com.example.skycast.utils.convertWindSpeed
import com.example.skycast.utils.getFormattedDate
import com.example.skycast.utils.getFormattedTime
import com.example.skycast.viewModel.SettingsViewModel
import kotlin.math.roundToInt

@SuppressLint("RememberReturnType")
@Composable
fun CurrentWeatherSection(weather: WeatherResponse, settingsViewModel: SettingsViewModel) {
    val currentTime = remember { getFormattedTime() }
    val currentDate = remember { getFormattedDate() }

    val temperatureUnit by settingsViewModel.temperatureUnit.collectAsState()
    val windSpeedUnit by settingsViewModel.windSpeedUnit.collectAsState()

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
            text = "$currentDate | $currentTime",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                ?: "Mostly Cloudy",
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

        WeatherStatsCard(weather, temperatureUnit, windSpeedUnit)
    }
}

@Composable
fun WeatherStatsCard(weather: WeatherResponse, temperatureUnit: String, windSpeedUnit: String) {
    val tempValue = convertTemperature(weather.main.temp, "celsius", temperatureUnit).roundToInt()
    val windSpeed = convertWindSpeed(weather.wind.speed, "kmh", windSpeedUnit).roundToInt()

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
                WeatherDetail(value = "$tempValue° $temperatureUnit", label = "Temperature")
                WeatherDetail(value = "$windSpeed $windSpeedUnit", label = "Wind Speed")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetail(value = "${weather.main.humidity}%", label = "Humidity")
                WeatherDetail(value = "${weather.main.pressure} hPa", label = "Pressure")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                WeatherDetail(value = "${weather.clouds.Clouds}%", label = "Cloud Coverage")
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
