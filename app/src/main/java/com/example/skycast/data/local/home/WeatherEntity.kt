package com.example.skycast.data.local.home

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skycast.data.model.Clouds
import com.example.skycast.data.model.DailyWeather
import com.example.skycast.data.model.ForecastItem
import com.example.skycast.data.model.MainInfo
import com.example.skycast.data.model.SysInfo
import com.example.skycast.data.model.WeatherInfo
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.data.model.WindInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val id: Int = 1,
    val temp: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val cityName: String,
    val description: String,
    val icon: String
)


@Entity(tableName = "hourly_forecast")
data class HourlyForecastEntity(
    @PrimaryKey val time: Long,
    val temp: Double,
    val humidity: Int,
    val icon: String
)


@Entity(tableName = "daily_forecast")
data class DailyForecastEntity(
    @PrimaryKey val date: Long,
    val minTemp: Double,
    val maxTemp: Double,
    val icon: String
)





fun WeatherResponse.toEntity(): WeatherEntity {
    return WeatherEntity(
        id = 1,
        temp = main.temp,
        humidity = main.humidity,
        pressure = main.pressure,
        windSpeed = wind.speed,
        cityName = name,
        description = weather.firstOrNull()?.description ?: "",
        icon = weather.firstOrNull()?.icon ?: ""
    )
}

fun ForecastItem.toHourlyEntity(): HourlyForecastEntity {
    return HourlyForecastEntity(
        time = dt,
        temp = main.temp,
        humidity = main.humidity,
        icon = weather.firstOrNull()?.icon ?: ""
    )
}

fun ForecastItem.toDailyEntity(): DailyForecastEntity {
    return DailyForecastEntity(
        date = dt,
        minTemp = main.temp,
        maxTemp = main.temp,
        icon = weather.firstOrNull()?.icon ?: ""
    )
}

fun WeatherEntity.toWeatherResponse(): WeatherResponse {
    return WeatherResponse(
        name = cityName,
        weather = listOf(
            WeatherInfo(
                id = 0,
                description = description,
                icon = icon
            )
        ),
        main = MainInfo(
            temp = temp,
            humidity = humidity,
            pressure = pressure
        ),
        wind = WindInfo(
            speed = windSpeed
        ),
        sys = SysInfo(
            sunrise = 0,
            sunset = 0
        ),
        clouds = Clouds(
            Clouds = 0
        )
    )
}

fun HourlyForecastEntity.toForecastItem(): ForecastItem {
    return ForecastItem(
        dt = time,
        main = MainInfo(
            temp = temp,
            humidity = humidity,
            pressure = 0
        ),
        weather = listOf(
            WeatherInfo(
                id = 0,
                description = "",
                icon = icon
            )
        ),
        dt_txt = ""
    )
}

fun DailyForecastEntity.toDailyWeather(): DailyWeather {
    val displayDate = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        .format(Date(this.date * 1000))
    return DailyWeather(
        date = displayDate,
        timestamp = this.date,
        maxTemp = this.maxTemp,
        minTemp = this.minTemp,
        weatherIcon = this.icon,
        description = ""
    )
}
