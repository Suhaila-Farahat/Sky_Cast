package com.example.skycast.data.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.skycast.data.local.LocalDataSource
import com.example.skycast.data.local.alert.WeatherAlert
import com.example.skycast.data.local.fav.FavoriteLocationEntity
import com.example.skycast.data.local.home.DailyForecastEntity
import com.example.skycast.data.local.home.HourlyForecastEntity
import com.example.skycast.data.local.home.WeatherEntity
import com.example.skycast.data.local.home.toWeatherResponse
import com.example.skycast.data.model.ForecastResponse
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.data.remote.RemoteDataSource
import com.example.skycast.workers.WeatherAlertWorker
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

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

    suspend fun get5DayForecast(lat: Double, lon: Double, apiKey: String): ForecastResponse {
        return remoteDataSource.get5DayForecast(lat, lon, apiKey)
    }

    // Local caching methods
    suspend fun cacheWeather(weatherEntity: WeatherEntity) {
        localDataSource.addWeatherData(weatherEntity)
    }

    suspend fun getCachedWeather(): WeatherResponse? {
        val cachedWeather = localDataSource.getLastWeatherData()
        return cachedWeather?.toWeatherResponse()
    }

    suspend fun cacheHourlyForecast(hourlyForecast: List<HourlyForecastEntity>) {
        localDataSource.addHourlyForecast(hourlyForecast)
    }

    suspend fun getCachedHourlyForecast(): List<HourlyForecastEntity> {
        return localDataSource.getHourlyForecast()
    }

    suspend fun cacheDailyForecast(dailyForecast: List<DailyForecastEntity>) {
        localDataSource.addDailyForecast(dailyForecast)
    }

    suspend fun getCachedDailyForecast(): List<DailyForecastEntity> {
        return localDataSource.getDailyForecast()
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

    // Alert
    suspend fun addWeatherAlert(alert: WeatherAlert) {
        localDataSource.addWeatherAlert(alert)
        if (sharedPreferences is Context) {
            scheduleWeatherAlert(alert, sharedPreferences)
        }
    }

    suspend fun removeWeatherAlert(alert: WeatherAlert) {
        localDataSource.removeWeatherAlert(alert)
    }

    fun getActiveWeatherAlerts(): Flow<List<WeatherAlert>> {
        return localDataSource.getActiveWeatherAlerts()
    }

    suspend fun deactivateWeatherAlert(alertId: Long) {
        localDataSource.deactivateWeatherAlert(alertId)
    }

    suspend fun getWeatherAlertById(alertId: Long): WeatherAlert? {
        return localDataSource.getWeatherAlertById(alertId)
    }

    private fun scheduleWeatherAlert(alert: WeatherAlert, context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
            .setInitialDelay(alert.time - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("alert_id" to alert.id))
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
