@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.skycast.view.favouriteScreen

import android.util.Log
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
import com.example.skycast.R
import com.example.skycast.data.local.FavoriteLocationEntity
import com.example.skycast.utils.WeatherIconUtil
import com.example.skycast.viewModel.HomeViewModel

@Composable
fun ForecastScreen(location: FavoriteLocationEntity, viewModel: HomeViewModel) {
    val weatherData by viewModel.weatherState.collectAsState(initial = null)
    val forecastData by viewModel.hourlyForecast.collectAsState(initial = null)

    LaunchedEffect(location.latitude, location.longitude) {
        Log.d("ForecastScreen", "Fetching weather for: ${location.name} (${location.latitude}, ${location.longitude})")
        viewModel.fetchWeatherByLocation(location.latitude, location.longitude)
        viewModel.fetchHourlyForecast(location.latitude, location.longitude)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Current Weather in ${location.name}", color = Color.White) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                weatherData?.let { weather ->
                    val icon = WeatherIconUtil.getWeatherIcon(weather.weather.firstOrNull()?.icon ?: "")

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = icon),
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )
                            Text(
                                text = "${weather.main.temp}°C",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = weather.weather.firstOrNull()?.description ?: "No data",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                } ?: Text("⚠ No weather data available", color = Color.White)

                Spacer(modifier = Modifier.height(16.dp))

                forecastData?.list?.let { list ->
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(list) { item ->
                            val icon = WeatherIconUtil.getWeatherIcon(item.weather.firstOrNull()?.icon ?: "")

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(text = item.dt_txt, fontSize = 16.sp, color = Color.White)
                                        Text(text = "${item.main.temp}°C", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                } ?: Text("No forecast data available", color = Color.White)
            }
        }
    }
}
