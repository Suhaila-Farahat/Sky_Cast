package com.example.skycast.data.local.alert

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class WeatherAlert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val time: Long,
    val alertMessage: String,
    val isActive: Boolean = true,
)
