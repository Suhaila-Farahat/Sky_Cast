package com.example.skycast.data.local.home

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("SELECT * FROM weather LIMIT 1")
    suspend fun getLastWeather(): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyForecast(hourlyForecast: List<HourlyForecastEntity>)

    @Query("SELECT * FROM hourly_forecast ORDER BY time ASC")
    suspend fun getHourlyForecast(): List<HourlyForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyForecast(dailyForecast: List<DailyForecastEntity>)

    @Query("SELECT * FROM daily_forecast ORDER BY date ASC")
    suspend fun getDailyForecast(): List<DailyForecastEntity>
}
