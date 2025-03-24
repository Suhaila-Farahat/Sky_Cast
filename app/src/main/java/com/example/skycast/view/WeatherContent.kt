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
import com.example.skycast.utils.WeatherIconUtil
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherContent(weather: WeatherResponse) {
    val currentDate by remember { mutableStateOf(getFormattedDate()) }
    val sunsetTime by remember { mutableStateOf(formatUnixTime(weather.sys.sunset)) }
    val weatherCondition by remember { mutableStateOf(getWeatherCondition(weather)) }
    val weatherIconId by rememberUpdatedState(WeatherIconUtil.getWeatherIcon(weather.weather.firstOrNull()?.icon ?: "01d"))

    val weatherIcon = loadWeatherIcon(weatherIconId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeatherHeader(city = weather.name, date = currentDate)
        Spacer(modifier = Modifier.height(16.dp))
        WeatherIconDisplay(weatherIcon)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = weatherCondition, color = Color.White, fontSize = 25.sp)
        WeatherStats(weather)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "\uD83C\uDF05 Sunset: $sunsetTime", color = Color.White, fontSize = 20.sp)
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
        Image(bitmap = weatherIcon, contentDescription = "Weather Icon", modifier = Modifier.size(200.dp))
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

fun formatUnixTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

fun getFormattedDate(): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
}

fun getWeatherCondition(weather: WeatherResponse): String {
    return weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercaseChar() } ?: "Unknown"
}
