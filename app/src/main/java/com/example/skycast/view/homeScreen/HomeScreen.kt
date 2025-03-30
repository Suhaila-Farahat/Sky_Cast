package com.example.skycast.view.homeScreen

import HourlyWeatherItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    val currentLocationState by viewModel.currentLocation.collectAsState(initial = null)

    LaunchedEffect(currentLocationState) {
        currentLocationState?.let { location ->
            viewModel.fetchWeatherByLocation(location.latitude, location.longitude)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))))
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            weatherState == null || hourlyForecastState == null -> CircularProgressIndicator(color = Color.White)
            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    CurrentWeatherSection(weatherState!!,settingsViewModel)

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(hourlyForecastState!!.list.map {
                            HourlyWeather(it.dt, it.main.temp, it.weather.firstOrNull()?.icon ?: "01d")
                        }) { hourlyWeather ->
                            HourlyWeatherItem(hourlyWeather ,settingsViewModel)
                        }
                    }
                }
            }
        }
    }
}



