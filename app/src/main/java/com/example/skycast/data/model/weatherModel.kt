package com.example.skycast.data.model

data class WeatherInfo(
    val id: Int,
    val description: String,
    val icon: String
)

data class MainInfo(
    val temp: Double,
    val humidity: Int,
    val pressure: Int

)

data class WindInfo(
    val speed: Double
)

data class SysInfo(
    val sunrise: Long,
    val sunset: Long
)

data class HourlyWeather(
    val timestamp: Long,
    val temperature: Double,
    val weatherIcon: String
)


data class WeatherResponse(
    val name: String,
    val weather: List<WeatherInfo>,
    val main: MainInfo,
    val wind: WindInfo,
    val sys: SysInfo,
    val clouds: Clouds,
)

data class Clouds(
    val Clouds: Int
)



data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: CityInfo
)

data class ForecastItem(
    val dt: Long,
    val main: MainInfo,
    val weather: List<WeatherInfo>,
    val dt_txt: String
)

data class CityInfo(
    val name: String,
    val country: String
)