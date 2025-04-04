package com.example.skycast.utils

import java.util.Locale
import android.content.Context
import android.content.SharedPreferences

class LanguageUtils(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun saveLanguage(languageCode: String) {
        val editor = prefs.edit()
        editor.putString("language", languageCode)
        editor.apply()
    }

    fun getSavedLanguage(): String {
        return prefs.getString("language", "en") ?: "en"
    }
}


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

