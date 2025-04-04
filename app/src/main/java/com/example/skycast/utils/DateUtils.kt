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
    // Set the locale based on the selected language
    val locale = when (language) {
        "ar" -> Locale("ar") // Arabic
        "en" -> Locale("en") // English
        else -> Locale.getDefault() // Default system locale
    }

    // Create the date and time format based on the selected language
    val dateFormat = SimpleDateFormat("EEEE, d MMM", locale) // "EEEE" gives the day name, "d MMM" gives the day and month
    val timeFormat = SimpleDateFormat("hh:mm a", locale) // "hh:mm a" gives the time in 12-hour format with AM/PM

    // Get the current date and time
    val currentDate = dateFormat.format(Date())
    val currentTime = timeFormat.format(Date())

    return Pair(currentDate, currentTime) // Return formatted date and time as a pair
}
