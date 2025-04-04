package com.example.skycast.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.*
import com.example.skycast.data.local.home.DailyForecastEntity
import com.example.skycast.data.local.home.toEntity
import com.example.skycast.data.local.home.toForecastItem
import com.example.skycast.data.local.home.toHourlyEntity
import com.example.skycast.data.model.*
import com.example.skycast.data.repo.WeatherRepository
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(
    application: Application,
    private val repository: WeatherRepository
) : AndroidViewModel(application) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val API_KEY = "c3b0faa25a8011e4d3ac4978f4b092f7"

    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState

    private val _hourlyForecast = MutableStateFlow<ForecastResponse?>(null)
    val hourlyForecast: StateFlow<ForecastResponse?> = _hourlyForecast

    private val _dailyForecast = MutableStateFlow<List<DailyWeather>>(emptyList())
    val dailyForecast: StateFlow<List<DailyWeather>> = _dailyForecast

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadCachedData()
    }

    private fun loadCachedData() {
        viewModelScope.launch {
            try {
                repository.getCachedWeather()?.let {
                    _weatherState.value = it
                }

                val cachedHourly = repository.getCachedHourlyForecast()
                if (cachedHourly.isNotEmpty()) {
                    _hourlyForecast.value = ForecastResponse(
                        list = cachedHourly.map { it.toForecastItem() },
                        city = CityInfo(name = "Unknown", country = "")
                    )
                }

                val cachedDaily = repository.getCachedDailyForecast()
                if (cachedDaily.isNotEmpty()) {
                    _dailyForecast.value = cachedDaily.map {
                        val displayDate = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
                            .format(Date(it.date * 1000))

                        DailyWeather(
                            date = displayDate,
                            timestamp = it.date,
                            maxTemp = it.maxTemp,
                            minTemp = it.minTemp,
                            weatherIcon = it.icon,
                            description = ""
                        )
                    }.sortedBy { it.timestamp }
                }

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load cached data: ${e.message}"
            }
        }
    }

    fun fetchWeather() {
        _isLoading.value = true
        _errorMessage.value = null
        getLastLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location == null) {
                    handleError("Unable to get current location")
                    return@addOnSuccessListener
                }

                updateCurrentLocation(location.latitude, location.longitude)
                fetchWeatherByLocation(location.latitude, location.longitude)
                fetchHourlyForecast(location.latitude, location.longitude)
                fetch5DayForecast(location.latitude, location.longitude)
            }
            .addOnFailureListener {
                handleError("Failed to get location: ${it.message}")
            }
    }

    fun updateCurrentLocation(lat: Double, lon: Double) {
        _currentLocation.value = Location("").apply {
            latitude = lat
            longitude = lon
        }
    }

    private fun handleError(message: String) {
        _isLoading.value = false
        _errorMessage.value = message
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val weather = repository.getWeather(lat, lon, API_KEY)
                _weatherState.value = weather
                repository.cacheWeather(weather.toEntity())
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch weather: ${e.message}"
                repository.getCachedWeather()?.let {
                    _weatherState.value = it
                }
            } finally {
                checkLoadingDone()
            }
        }
    }

    fun fetchHourlyForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val forecast = repository.getHourlyForecast(lat, lon, API_KEY)
                _hourlyForecast.value = forecast
                repository.cacheHourlyForecast(forecast.list.map { it.toHourlyEntity() })
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch hourly forecast: ${e.message}"
                if (_hourlyForecast.value == null) {
                    val cached = repository.getCachedHourlyForecast()
                    if (cached.isNotEmpty()) {
                        _hourlyForecast.value = ForecastResponse(
                            list = cached.map { it.toForecastItem() },
                            city = CityInfo(name = "Unknown", country = "")
                        )
                    }
                }
            } finally {
                checkLoadingDone()
            }
        }
    }

    fun fetch5DayForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = repository.get5DayForecast(lat, lon, API_KEY)
                val processed = processDailyForecastData(response.list)
                _dailyForecast.value = processed
                repository.cacheDailyForecast(processDailyForecastEntities(response.list))
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch 5-day forecast: ${e.message}"
                if (_dailyForecast.value.isEmpty()) {
                    val cached = repository.getCachedDailyForecast()
                    if (cached.isNotEmpty()) {
                        _dailyForecast.value = cached.map {
                            val displayDate = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
                                .format(Date(it.date * 1000))

                            DailyWeather(
                                date = displayDate,
                                timestamp = it.date,
                                maxTemp = it.maxTemp,
                                minTemp = it.minTemp,
                                weatherIcon = it.icon,
                                description = ""
                            )
                        }.sortedBy { it.timestamp }
                    }
                }
            } finally {
                checkLoadingDone()
            }
        }
    }

    private fun checkLoadingDone() {
        if (_weatherState.value != null &&
            _hourlyForecast.value != null &&
            _dailyForecast.value.isNotEmpty()
        ) {
            _isLoading.value = false
        }
    }

    private fun processDailyForecastData(items: List<ForecastItem>): List<DailyWeather> {
        val dailyMap = items.groupBy {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.dt * 1000))
        }

        return dailyMap.map { (_, dayItems) ->
            val max = dayItems.maxOfOrNull { it.main.temp } ?: 0.0
            val min = dayItems.minOfOrNull { it.main.temp } ?: 0.0
            val noonItem = dayItems.find {
                val hour = SimpleDateFormat("HH", Locale.getDefault()).format(Date(it.dt * 1000)).toInt()
                hour in 11..13
            } ?: dayItems.first()

            val icon = noonItem.weather.firstOrNull()?.icon ?: "01d"
            val desc = noonItem.weather.firstOrNull()?.description ?: "Clear"
            val date = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(noonItem.dt * 1000))

            DailyWeather(
                date = date,
                timestamp = noonItem.dt,
                maxTemp = max,
                minTemp = min,
                weatherIcon = icon,
                description = desc
            )
        }.sortedBy { it.timestamp }
    }

    private fun processDailyForecastEntities(items: List<ForecastItem>): List<DailyForecastEntity> {
        val dailyMap = items.groupBy {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.dt * 1000))
        }

        return dailyMap.map { (_, dayItems) ->
            val max = dayItems.maxOfOrNull { it.main.temp } ?: 0.0
            val min = dayItems.minOfOrNull { it.main.temp } ?: 0.0
            val noonItem = dayItems.find {
                val hour = SimpleDateFormat("HH", Locale.getDefault()).format(Date(it.dt * 1000)).toInt()
                hour in 11..13
            } ?: dayItems.first()

            DailyForecastEntity(
                date = noonItem.dt,
                maxTemp = max,
                minTemp = min,
                icon = noonItem.weather.firstOrNull()?.icon ?: "01d"
            )
        }
    }
}

class HomeViewModelFactory(
    private val application: Application,
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(application, repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
