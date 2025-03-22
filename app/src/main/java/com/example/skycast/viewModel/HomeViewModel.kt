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

    private val _formattedDate = MutableStateFlow(getFormattedDate())
    val formattedDate: StateFlow<String> = _formattedDate

    private val _weatherCondition = MutableStateFlow("Unknown")
    val weatherCondition: StateFlow<String> = _weatherCondition

    private val _sunsetTime = MutableStateFlow("")
    val sunsetTime: StateFlow<String> = _sunsetTime

    private val _weatherIconId = MutableStateFlow(WeatherIconUtil.getWeatherIcon("01d"))
    val weatherIconId: StateFlow<Int> = _weatherIconId

    fun fetchWeather(city: String) {
        val apiKey = "8755b9ff4dd0618b1c87b51cb91b4044"
        viewModelScope.launch {
            try {
                val weather = repository.getWeather(city, apiKey)
                _weatherState.value = weather
                _sunsetTime.value = formatUnixTime(weather.sys.sunset)
                _weatherCondition.value = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercaseChar() } ?: "Unknown"
                _weatherIconId.value = WeatherIconUtil.getWeatherIcon(weather.weather.firstOrNull()?.icon ?: "01d")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun formatUnixTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    private fun getFormattedDate(): String {
        return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
    }
}
