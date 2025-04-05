package com.example.skycast.utils


import java.text.SimpleDateFormat
import java.util.*

fun getFormattedDate(): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}


fun getFormattedTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}


fun getFormattedDateAndTime(language: String): Pair<String, String> {
    val locale = when (language) {
        "ar" -> Locale("ar")
        "en" -> Locale("en")
        else -> Locale.getDefault()
    }

    val dateFormat = SimpleDateFormat("EEEE, d MMM", locale)
    val timeFormat = SimpleDateFormat("hh:mm a", locale)

    val currentDate = dateFormat.format(Date())
    val currentTime = timeFormat.format(Date())

    return Pair(currentDate, currentTime)
}
