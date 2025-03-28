package com.example.skycast.view.homeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.data.model.HourlyWeather
import com.example.skycast.utils.WeatherIconUtil
import com.example.skycast.utils.formatTimestamp
import kotlin.math.roundToInt


@Composable
fun HourlyWeatherItem(weather: HourlyWeather) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF334155)),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .width(120.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = WeatherIconUtil.getWeatherIcon(weather.weatherIcon)),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(65.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${weather.temperature.roundToInt()}°C",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatTimestamp(weather.timestamp),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}



