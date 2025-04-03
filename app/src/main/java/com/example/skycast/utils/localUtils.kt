package com.example.skycast.utils

fun getWeatherDescriptionInArabic(description: String): String {
    return when (description.lowercase()) {
        "clear sky" -> "سماء صافية"
        "few clouds" -> "قليل من الغيوم"
        "scattered clouds" -> "غيوم متفرقة"
        "broken clouds" -> "غيوم مكسورة"
        "shower rain" -> "مطر غزير"
        "rain" -> "مطر"
        "thunderstorm" -> "عاصفة رعدية"
        "snow" -> "ثلج"
        "mist" -> "ضباب"
        else -> description
    }
}
