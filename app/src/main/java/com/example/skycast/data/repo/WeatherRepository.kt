package com.example.skycast.data.repo

import android.content.SharedPreferences
import com.example.skycast.data.local.FavoriteLocationEntity
import com.example.skycast.data.local.LocalDataSource
import com.example.skycast.data.model.ForecastResponse
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val sharedPreferences: SharedPreferences
) {

    // Remote API Calls
    suspend fun getWeather(lat: Double, lon: Double, apiKey: String): WeatherResponse {
        return remoteDataSource.getCurrentWeather(lat, lon, apiKey)
    }

    suspend fun getHourlyForecast(lat: Double, lon: Double, apiKey: String): ForecastResponse {
        return remoteDataSource.getHourlyForecast(lat, lon, apiKey)
    }

    // Favorite Location Operations
    suspend fun addFavoriteLocation(location: FavoriteLocationEntity) {
        localDataSource.addFavoriteLocation(location)
    }

    suspend fun removeFavoriteLocation(location: FavoriteLocationEntity) {
        localDataSource.removeFavoriteLocation(location)
    }

    fun getFavoriteLocations(): Flow<List<FavoriteLocationEntity>> {
        return localDataSource.getFavoriteLocations()
    }

    // User Settings (SharedPreferences)
    companion object {
        private const val LOCATION_MODE_KEY = "location_mode"
        private const val TEMPERATURE_UNIT_KEY = "temperature_unit"
        private const val WIND_SPEED_UNIT_KEY = "wind_speed_unit"
        private const val LANGUAGE_KEY = "language"
    }

    fun getLocationMode(): String {
        return sharedPreferences.getString(LOCATION_MODE_KEY, "GPS") ?: "GPS"
    }

    fun setLocationMode(mode: String) {
        sharedPreferences.edit().putString(LOCATION_MODE_KEY, mode).apply()
    }

    fun getTemperatureUnit(): String {
        return sharedPreferences.getString(TEMPERATURE_UNIT_KEY, "Celsius") ?: "Celsius"
    }

    fun setTemperatureUnit(unit: String) {
        sharedPreferences.edit().putString(TEMPERATURE_UNIT_KEY, unit).apply()
    }

    fun getWindSpeedUnit(): String {
        return sharedPreferences.getString(WIND_SPEED_UNIT_KEY, "meter/sec") ?: "meter/sec"
    }

    fun setWindSpeedUnit(unit: String) {
        sharedPreferences.edit().putString(WIND_SPEED_UNIT_KEY, unit).apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString(LANGUAGE_KEY, "English") ?: "English"
    }

    fun setLanguage(lang: String) {
        sharedPreferences.edit().putString(LANGUAGE_KEY, lang).apply()
    }
}
