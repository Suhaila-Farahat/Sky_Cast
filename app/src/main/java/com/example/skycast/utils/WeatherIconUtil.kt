package com.example.skycast.utils

import com.example.skycast.R

object WeatherIconUtil {
    fun getWeatherIcon(conditionCode: String): Int {
        return when (conditionCode) {
            "01d" -> R.drawable.ic_sunny
            "01n" -> R.drawable.ic_clear_night

            "02d" -> R.drawable.ic_few_clouds_day
            "02n" -> R.drawable.ic_few_clouds_night

            "03d", "03n" -> R.drawable.ic_scattered_clouds

            "04d" -> R.drawable.ic_cloudy
            "04n" -> R.drawable.ic_cloudy_night

            "09d", "09n" -> R.drawable.ic_shower_rain

            "10d" -> R.drawable.ic_rain
            "10n" -> R.drawable.ic_rain_night

            "11d", "11n" -> R.drawable.ic_thunderstorm

            "13d", "13n" -> R.drawable.ic_snow

            "50d", "50n" -> R.drawable.ic_fog

            else -> R.drawable.ic_noweather
        }
    }
}
