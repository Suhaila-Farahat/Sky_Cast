@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.data.local.FavoriteLocationEntity
import com.example.skycast.utils.WeatherIconUtil
import com.example.skycast.utils.convertTemperature
import com.example.skycast.viewModel.HomeViewModel
import com.example.skycast.viewModel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun ForecastScreen(location: FavoriteLocationEntity, viewModel: HomeViewModel, settingsViewModel: SettingsViewModel) {
    val weatherData by viewModel.weatherState.collectAsState(initial = null)
    val forecastData by viewModel.hourlyForecast.collectAsState(initial = null)
    val temperatureUnit by settingsViewModel.temperatureUnit.collectAsState()

    LaunchedEffect(location.latitude, location.longitude) {
        viewModel.fetchWeatherByLocation(location.latitude, location.longitude)
        viewModel.fetchHourlyForecast(location.latitude, location.longitude)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forecast in ${location.name}", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    )
                )
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    weatherData?.let { weather ->
                        val icon = WeatherIconUtil.getWeatherIcon(weather.weather.firstOrNull()?.icon ?: "")
                        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
                        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                        val tempValue = convertTemperature(weather.main.temp, "celsius", temperatureUnit).roundToInt()

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("${tempValue}°$temperatureUnit", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(weather.weather.firstOrNull()?.description ?: "No data", fontSize = 18.sp, color = Color.White)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(dateFormat, fontSize = 14.sp, color = Color.LightGray)
                                    Text(timeFormat, fontSize = 14.sp, color = Color.LightGray)
                                    Text("💧 Humidity: ${weather.main.humidity}%", fontSize = 14.sp, color = Color.LightGray)
                                    Text("🌬 Wind: ${weather.wind.speed} m/s", fontSize = 14.sp, color = Color.LightGray)
                                    Text("🌡 Pressure: ${weather.main.pressure} hPa", fontSize = 14.sp, color = Color.LightGray)
                                    Text("☁ Clouds: ${weather.clouds.Clouds}%", fontSize = 14.sp, color = Color.LightGray)
                                }
                            }
                        }
                    } ?: Text("No weather data available", color = Color.White)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item { Text("Hourly Forecast", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.fillMaxWidth()) }

                forecastData?.list?.let { list ->
                    items(list) { item ->
                        val icon = WeatherIconUtil.getWeatherIcon(item.weather.firstOrNull()?.icon ?: "")
                        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(item.dt * 1000L))
                        val tempValue = convertTemperature(item.main.temp, "celsius", temperatureUnit).roundToInt()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Image(
                                    painter = painterResource(id = icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp)
                                )
                                Text(text = "${tempValue}°$temperatureUnit", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = timeFormat, fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                } ?: item { Text("No forecast data available", color = Color.White) }
            }
        }
    }
}
