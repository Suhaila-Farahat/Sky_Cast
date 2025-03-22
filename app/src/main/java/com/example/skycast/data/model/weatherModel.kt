package com.example.skycast.data.model

data class WeatherInfo(
    val id: Int,
    val description: String,
    val icon: String
)

data class MainInfo(
    val temp: Double,
    val humidity: Int
)

data class WindInfo(
    val speed: Double
)

data class SysInfo(
    val sunrise: Long,
    val sunset: Long
)
