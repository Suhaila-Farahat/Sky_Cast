package com.example.skycast.data.local

import com.example.skycast.data.local.alert.WeatherAlert
import com.example.skycast.data.local.alert.WeatherAlertDao
import com.example.skycast.data.local.fav.FavoriteLocationDao
import com.example.skycast.data.local.fav.FavoriteLocationEntity
import com.example.skycast.data.local.home.DailyForecastEntity
import com.example.skycast.data.local.home.HourlyForecastEntity
import com.example.skycast.data.local.home.WeatherDao
import com.example.skycast.data.local.home.WeatherEntity
import kotlinx.coroutines.flow.Flow


class LocalDataSource(
    private val favoriteLocationDao: FavoriteLocationDao,
    private val weatherAlertDao: WeatherAlertDao,
    private val weatherDao: WeatherDao
) {

// fav
    suspend fun addFavoriteLocation(location: FavoriteLocationEntity) {
        favoriteLocationDao.insertFavoriteLocation(location)
    }

    suspend fun removeFavoriteLocation(location: FavoriteLocationEntity) {
        favoriteLocationDao.deleteFavoriteLocation(location)
    }

    fun getFavoriteLocations() = favoriteLocationDao.getAllFavoriteLocations()
// alert
    suspend fun addWeatherAlert(alert: WeatherAlert) {
        weatherAlertDao.insertWeatherAlert(alert)
    }

    suspend fun removeWeatherAlert(alert: WeatherAlert) {
        weatherAlertDao.deleteWeatherAlert(alert)
    }

    fun getActiveWeatherAlerts(): Flow<List<WeatherAlert>> {
        return weatherAlertDao.getActiveWeatherAlerts()
    }

    suspend fun deactivateWeatherAlert(alertId: Long) {
        weatherAlertDao.deactivateAlert(alertId)
    }

    suspend fun getWeatherAlertById(alertId: Long): WeatherAlert? {
        return weatherAlertDao.getWeatherAlertById(alertId)
    }

    //weather
    suspend fun addWeatherData(weather: WeatherEntity) {
        weatherDao.insertWeather(weather)
    }

    suspend fun getLastWeatherData(): WeatherEntity? {
        return weatherDao.getLastWeather()
    }

    suspend fun addHourlyForecast(hourlyForecast: List<HourlyForecastEntity>) {
        weatherDao.insertHourlyForecast(hourlyForecast)
    }

    suspend fun getHourlyForecast(): List<HourlyForecastEntity> {
        return weatherDao.getHourlyForecast()
    }

    suspend fun addDailyForecast(dailyForecast: List<DailyForecastEntity>) {
        weatherDao.insertDailyForecast(dailyForecast)
    }

    suspend fun getDailyForecast(): List<DailyForecastEntity> {
        return weatherDao.getDailyForecast()
    }
}
