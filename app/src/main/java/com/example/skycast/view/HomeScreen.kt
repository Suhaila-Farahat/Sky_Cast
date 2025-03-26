package com.example.skycast.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skycast.viewModel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val weatherState by viewModel.weatherState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchWeather()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF000033), Color(0xFF006699))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        weatherState?.let {
            WeatherContent(it)
        } ?: CircularProgressIndicator(color = Color.White)
    }
}
