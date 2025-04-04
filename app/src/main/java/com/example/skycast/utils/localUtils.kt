package com.example.skycast.utils

import java.util.Locale

fun getWeatherDescription(weatherDescription: String, locale: Locale): String {
    return if (locale.language == "ar") {
        translateToArabic(weatherDescription)
    } else {
        weatherDescription
    }
}

fun translateToArabic(englishDescription: String): String {
    return when (englishDescription.toLowerCase()) {
        "clear sky" -> "سماء صافية"
        "few clouds" -> "قليل من الغيوم"
        "scattered clouds" -> "غيوم متفرقة"
        "broken clouds" -> "غيوم مكسورة"
        "shower rain" -> "أمطار خفيفة"
        "rain" -> "أمطار"
        "thunderstorm" -> "عاصفة رعدية"
        "snow" -> "ثلج"
        "mist" -> "ضباب"
        else -> englishDescription
    }
}

