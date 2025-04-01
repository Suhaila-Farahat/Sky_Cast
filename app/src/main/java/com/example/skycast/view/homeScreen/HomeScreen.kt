package com.example.skycast.view.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.data.model.HourlyWeather
import com.example.skycast.viewModel.HomeViewModel
import com.example.skycast.viewModel.SettingsViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel
) {
    val weatherState by viewModel.weatherState.collectAsState()
    val hourlyForecastState by viewModel.hourlyForecast.collectAsState()
    val dailyForecastState by viewModel.dailyForecast.collectAsState()
    val currentLocationState by viewModel.currentLocation.collectAsState(initial = null)

    LaunchedEffect(currentLocationState) {
        currentLocationState?.let { location ->
            viewModel.fetchWeatherByLocation(location.latitude, location.longitude)
            viewModel.fetchHourlyForecast(location.latitude, location.longitude)
            viewModel.fetch5DayForecast(location.latitude, location.longitude)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)))),
        contentAlignment = Alignment.Center // Ensures loading indicator is centered
    ) {
        when {
            weatherState == null || hourlyForecastState == null || dailyForecastState.isEmpty() -> {
                // Show progress indicator in the center of the screen
                CircularProgressIndicator(color = Color.White)
            }
            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    CurrentWeatherSection(weatherState!!, settingsViewModel)

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.White.copy(alpha = 0.2f)
                    )

                    Text(
                        text = "Hourly Forecast",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(vertical = 16.dp)
                    )

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(hourlyForecastState!!.list.take(8).map {
                            HourlyWeather(it.dt, it.main.temp, it.weather.firstOrNull()?.icon ?: "01d")
                        }) { hourlyWeather ->
                            HourlyWeatherItem(hourlyWeather, settingsViewModel)
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.White.copy(alpha = 0.2f)
                    )

                    Text(
                        text = "5-Day Forecast",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(vertical = 16.dp)
                    )

                    DailyForecastSection(
                        dailyForecasts = dailyForecastState,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}
