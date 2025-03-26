package com.example.skycast.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skycast.data.model.HourlyWeather
import com.example.skycast.viewmodel.HomeViewModel
import com.example.skycast.utils.getFormattedDate

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val weatherState by viewModel.weatherState.collectAsState()
    val hourlyForecastState by viewModel.hourlyForecast.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)) // Dark gradient
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            weatherState == null || hourlyForecastState == null -> {
                CircularProgressIndicator(color = Color.White)
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    CurrentWeatherSection(weatherState!!)

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(hourlyForecastState!!.list.map { forecastItem ->
                            HourlyWeather(
                                timestamp = forecastItem.dt,
                                temperature = forecastItem.main.temp,
                                weatherIcon = forecastItem.weather.firstOrNull()?.icon ?: "01d"
                            )
                        }) { hourlyWeather ->
                            HourlyWeatherItem(hourlyWeather)
                        }
                    }
                }
            }
        }
    }
}

