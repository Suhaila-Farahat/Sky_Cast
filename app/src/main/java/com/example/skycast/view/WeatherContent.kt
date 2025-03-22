package com.example.skycast.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.data.remote.WeatherResponse
import com.example.skycast.viewModel.HomeViewModel

@Composable
fun WeatherContent(viewModel: HomeViewModel) {
    val weatherState by viewModel.weatherState.collectAsState()
    val currentDate by viewModel.formattedDate.collectAsState()
    val sunsetTime by viewModel.sunsetTime.collectAsState()
    val weatherCondition by viewModel.weatherCondition.collectAsState()
    val weatherIconId by viewModel.weatherIconId.collectAsState()

    val weatherIcon = loadWeatherIcon(weatherIconId)

    weatherState?.let { weather ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeatherHeader(city = weather.name, date = currentDate)
            Spacer(modifier = Modifier.height(8.dp))
            WeatherIconDisplay(weatherIcon)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = weatherCondition, color = Color.White, fontSize = 25.sp)
            WeatherStats(weather)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "\uD83C\uDF05 Sunset: $sunsetTime", color = Color.White, fontSize = 20.sp)
        }
    }
}

@Composable
fun WeatherHeader(city: String, date: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(city, color = Color.White, fontSize = 38.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(date, color = Color.White.copy(alpha = 0.7f), fontSize = 20.sp)
    }
}

@Composable
fun WeatherIconDisplay(weatherIcon: ImageBitmap?) {
    if (weatherIcon != null) {
        Image(bitmap = weatherIcon, contentDescription = "Weather Icon", modifier = Modifier.size(230.dp))
    } else {
        Text("Icon not found", color = Color.White, fontSize = 20.sp)
    }
}

@Composable
fun WeatherStats(weather: WeatherResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        WeatherStat(label = "Temp", value = "${weather.main.temp.toInt()}°C")
        WeatherStat(label = "Wind", value = "${weather.wind.speed.toInt()} km/h")
        WeatherStat(label = "Humidity", value = "${weather.main.humidity}%")
    }
}

@Composable
fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = Color.White, fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}

@Composable
fun loadWeatherIcon(weatherIconId: Int): ImageBitmap? {
    val context = LocalContext.current
    var weatherIcon by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(weatherIconId) {
        weatherIcon = try {
            val options = BitmapFactory.Options().apply { inSampleSize = 2 }
            BitmapFactory.decodeResource(context.resources, weatherIconId, options)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
    return weatherIcon
}
