package com.example.skycast.data.local

import com.example.skycast.data.local.alert.WeatherAlert
import com.example.skycast.data.local.alert.WeatherAlertDao
import com.example.skycast.data.local.fav.FavoriteLocationDao
import com.example.skycast.data.local.fav.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow


class LocalDataSource(
    private val favoriteLocationDao: FavoriteLocationDao,
    private val weatherAlertDao: WeatherAlertDao
) {

// fav
    suspend fun addFavoriteLocation(location: FavoriteLocationEntity) {
        favoriteLocationDao.insertFavoriteLocation(location)
    }

    suspend fun removeFavoriteLocation(location: FavoriteLocationEntity) {
        favoriteLocationDao.deleteFavoriteLocation(location)
    }

    fun getFavoriteLocations() = favoriteLocationDao.getAllFavoriteLocations()

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
}
