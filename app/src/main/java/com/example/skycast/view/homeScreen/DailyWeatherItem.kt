package com.example.skycast.view.homeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.data.model.DailyWeather
import com.example.skycast.viewModel.SettingsViewModel
import coil.compose.AsyncImage
import com.example.skycast.utils.WeatherIconUtil
import com.example.skycast.utils.convertTemperature

@Composable
fun DailyWeatherItem(
    dailyWeather: DailyWeather,
    settingsViewModel: SettingsViewModel
) {
    val temperatureUnit by settingsViewModel.temperatureUnit.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF334155).copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dailyWeather.date,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier.width(120.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val weatherIconRes = WeatherIconUtil.getWeatherIcon(dailyWeather.weatherIcon)
                AsyncImage(
                    model = weatherIconRes,
                    contentDescription = dailyWeather.description,
                    modifier = Modifier.size(50.dp)
                )


            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val maxTemp = convertTemperature(dailyWeather.maxTemp, "celsius", temperatureUnit)
                val minTemp = convertTemperature(dailyWeather.minTemp, "celsius", temperatureUnit)

                val unit = when (temperatureUnit) {
                    "Fahrenheit" -> "°F"
                    "Kelvin" -> "K"
                    else -> "°C"
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = dailyWeather.description.replaceFirstChar { it.uppercase() },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${maxTemp.toInt()}$unit / ${minTemp.toInt()}$unit",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

        }
    }
}

@Composable
fun DailyForecastSection(
    dailyForecasts: List<DailyWeather>,
    settingsViewModel: SettingsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        dailyForecasts.forEach { dailyWeather ->
            DailyWeatherItem(
                dailyWeather = dailyWeather,
                settingsViewModel = settingsViewModel
            )
        }
    }
}
