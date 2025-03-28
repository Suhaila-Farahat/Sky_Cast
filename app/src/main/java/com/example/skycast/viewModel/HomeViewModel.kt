package com.example.skycast.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.model.ForecastResponse
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.data.repo.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application, private val repository: WeatherRepository) :
    AndroidViewModel(application) {

    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState

    private val _hourlyForecast = MutableStateFlow<ForecastResponse?>(null)
    val hourlyForecast: StateFlow<ForecastResponse?> = _hourlyForecast

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
            try {
                Log.d("HomeViewModel", "Fetching weather for: ($lat, $lon)")
                val weather = repository.getWeather(lat, lon, API_KEY)
                _weatherState.value = weather
                Log.d("HomeViewModel", "✅ Weather updated: ${weather.main.temp}°C, ${weather.weather.firstOrNull()?.description}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ Error fetching weather: ${e.message}")
            }
        }
    }

    fun fetchHourlyForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Fetching hourly forecast for: ($lat, $lon)")
                val forecast = repository.getHourlyForecast(lat, lon, API_KEY)
                _hourlyForecast.value = forecast
                Log.d("HomeViewModel", "✅ Forecast updated: ${forecast.list.size} entries")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ Error fetching forecast: ${e.message}")
            }
        }
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
