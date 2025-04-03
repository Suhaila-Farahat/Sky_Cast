package com.example.skycast.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.model.DailyWeather
import com.example.skycast.data.model.ForecastItem
import com.example.skycast.data.model.ForecastResponse
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.data.repo.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(application: Application, private val repository: WeatherRepository) :
    AndroidViewModel(application) {

    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState

    private val _hourlyForecast = MutableStateFlow<ForecastResponse?>(null)
    val hourlyForecast: StateFlow<ForecastResponse?> = _hourlyForecast

    private val _dailyForecast = MutableStateFlow<List<DailyWeather>>(emptyList())
    val dailyForecast: StateFlow<List<DailyWeather>> = _dailyForecast

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val API_KEY = "c3b0faa25a8011e4d3ac4978f4b092f7"

    fun fetchWeather() {
        println("🔄 Debug: Starting fetchWeather()...")
        getLastLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location == null) return@addOnSuccessListener
            updateCurrentLocation(location.latitude, location.longitude)
            fetchWeatherByLocation(location.latitude, location.longitude)
            fetchHourlyForecast(location.latitude, location.longitude)
            fetch5DayForecast(location.latitude, location.longitude)
        }.addOnFailureListener {
            Toast.makeText(getApplication(), "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateCurrentLocation(lat: Double, lon: Double) {
        _currentLocation.value = Location("").apply {
            latitude = lat
            longitude = lon
        }
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
                val weather = repository.getWeather(lat, lon, API_KEY)
                _weatherState.value = weather
        }
    }

    fun fetchHourlyForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
                val forecast = repository.getHourlyForecast(lat, lon, API_KEY)
                _hourlyForecast.value = forecast
        }
    }

    fun fetch5DayForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
                val forecastResponse = repository.get5DayForecast(lat, lon, API_KEY)
                val dailyForecasts = processDailyForecastData(forecastResponse.list)
                _dailyForecast.value = dailyForecasts

        }
    }

    private fun processDailyForecastData(forecastItems: List<ForecastItem>): List<DailyWeather> {
        val dailyMap = mutableMapOf<String, MutableList<ForecastItem>>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (item in forecastItems) {
            val date = dateFormat.format(Date(item.dt * 1000))
            if (!dailyMap.containsKey(date)) {
                dailyMap[date] = mutableListOf()
            }
            dailyMap[date]?.add(item)
        }

        return dailyMap.entries.map { (date, items) ->
            val maxTemp = items.maxOfOrNull { it.main.temp } ?: 0.0
            val minTemp = items.minOfOrNull { it.main.temp } ?: 0.0

            val noonItem = items.find {
                val itemHour = SimpleDateFormat("HH", Locale.getDefault())
                    .format(Date(it.dt * 1000)).toInt()
                itemHour in 11..13
            } ?: items.first()

            val weatherInfo = noonItem.weather.firstOrNull()

            val displayDate = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
                .format(Date(noonItem.dt * 1000))

            DailyWeather(
                date = displayDate,
                timestamp = noonItem.dt,
                maxTemp = maxTemp,
                minTemp = minTemp,
                weatherIcon = weatherInfo?.icon ?: "01d",
                description = weatherInfo?.description ?: "Clear"
            )
        }.sortedBy { it.timestamp }
    }
}

class HomeViewModelFactory(
    private val application: Application,
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}