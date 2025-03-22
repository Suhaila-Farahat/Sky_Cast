package com.example.skycast.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.remote.WeatherResponse
import com.example.skycast.data.repo.WeatherRepository
import com.example.skycast.utils.WeatherIconUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(private val repository: WeatherRepository = WeatherRepository()) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState

    private val _hourlyForecast = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val hourlyForecast: StateFlow<List<Pair<String, Int>>> = _hourlyForecast

    fun fetchWeather(city: String) {
        val apiKey = "8755b9ff4dd0618b1c87b51cb91b4044"
        viewModelScope.launch {
            try {
                val weather = repository.getWeather(city, apiKey)
                _weatherState.value = weather

                // Generate mock hourly data (Replace this with real API response when available)
                _hourlyForecast.value = listOf(
                    "14:00" to (weather.main.temp.toInt() + 1),
                    "15:00" to weather.main.temp.toInt(),
                    "16:00" to (weather.main.temp.toInt() - 1)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
