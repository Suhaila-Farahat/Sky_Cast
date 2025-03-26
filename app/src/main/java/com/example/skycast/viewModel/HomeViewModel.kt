package com.example.skycast.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.model.ForecastResponse
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.data.remote.RemoteDataSource
import com.example.skycast.repository.WeatherRepository
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
            fetchWeatherByLocation(location.latitude, location.longitude)
            fetchHourlyForecast(location.latitude, location.longitude)
        }.addOnFailureListener {
            println("⚠️ Failed to get location: ${it.message}")
        }
    }

    private fun fetchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
                val weather = repository.getWeather(lat, lon, API_KEY)
                _weatherState.value = weather

        }
    }

    private fun fetchHourlyForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
                val forecast = repository.getHourlyForecast(lat, lon, API_KEY)
                _hourlyForecast.value = forecast

        }
    }
}



class HomeViewModelFactory(
    private val application: Application,
    private val remoteDataSource: RemoteDataSource
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(application, WeatherRepository(remoteDataSource)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
