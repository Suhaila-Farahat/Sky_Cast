package com.example.skycast.data.local.alert

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherAlertDao {

    @Insert
    suspend fun insertWeatherAlert(weatherAlert: WeatherAlert)

    @Delete
    suspend fun deleteWeatherAlert(weatherAlert: WeatherAlert)

    @Query("SELECT * FROM weather_alerts WHERE isActive = 1")
    fun getActiveWeatherAlerts(): Flow<List<WeatherAlert>>

    @Query("UPDATE weather_alerts SET isActive = 0 WHERE id = :alertId")
    suspend fun deactivateAlert(alertId: Long)

    @Query("SELECT * FROM weather_alerts WHERE id = :alertId")
    suspend fun getWeatherAlertById(alertId: Long): WeatherAlert?
}
