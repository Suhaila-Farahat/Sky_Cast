package com.example.skycast.view.homeScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.R
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.utils.LanguageUtils
import com.example.skycast.utils.WeatherIconUtil
import com.example.skycast.utils.convertTemperature
import com.example.skycast.utils.convertWindSpeed
import com.example.skycast.viewModel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@SuppressLint("RememberReturnType")
@Composable
fun CurrentWeatherSection(
    weather: WeatherResponse,
    settingsViewModel: SettingsViewModel,
    languageUtils: LanguageUtils // Injecting language utils to get saved language
) {
    // Get the saved language from SharedPreferences
    val language = languageUtils.getSavedLanguage()

    // Conditionally format the date and time based on the saved language
    val currentTime = remember { getFormattedTime(language) }
    val currentDate = remember { getFormattedDate(language) }

    // Fetch temperature and wind speed settings from ViewModel
    val temperatureUnit by settingsViewModel.temperatureUnit.collectAsState()
    val windSpeedUnit by settingsViewModel.windSpeedUnit.collectAsState()

    // Define the temperature unit symbol
    val tempUnitSymbol = when (temperatureUnit) {
        "Fahrenheit" -> "°F"
        "Kelvin" -> "K"
        else -> "°C"
    }

    // Composable layout
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Display city name
        Text(
            text = weather.name,
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )

        // Display current date and time
        Text(
            text = "$currentDate || $currentTime",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Display weather description
        Text(
            text = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                ?: stringResource(id = R.string.no_description),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display weather icon
        Image(
            painter = painterResource(id = WeatherIconUtil.getWeatherIcon(weather.weather.firstOrNull()?.icon ?: "01d")),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(160.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display weather stats card
        WeatherStatsCard(weather, settingsViewModel)
    }
}

// Function to get formatted date based on the selected language
fun getFormattedDate(languageCode: String): String {
    val locale = Locale(languageCode)  // Use the selected language
    val dateFormat = SimpleDateFormat("EEEE, d MMM", locale)
    return dateFormat.format(Date())
}

// Function to get formatted time based on the selected language
fun getFormattedTime(languageCode: String): String {
    val locale = Locale(languageCode)  // Use the selected language
    val timeFormat = SimpleDateFormat("hh:mm a", locale)
    return timeFormat.format(Date())
}

@Composable
fun WeatherStatsCard(weather: WeatherResponse, settingsViewModel: SettingsViewModel) {
    val tempValue = convertTemperature(weather.main.temp, "celsius", settingsViewModel.temperatureUnit.value).roundToInt()
    val windSpeed = convertWindSpeed(weather.wind.speed, "kmh", settingsViewModel.windSpeedUnit.value).roundToInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetail(value = "$tempValue°", label = stringResource(id = R.string.temperature))
                WeatherDetail(value = "$windSpeed km/h", label = stringResource(id = R.string.wind_speed))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetail(value = "${weather.main.humidity}%", label = stringResource(id = R.string.humidity))
                WeatherDetail(value = "${weather.main.pressure}hPa", label = stringResource(id = R.string.pressure))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                WeatherDetail(value = "${weather.clouds.Clouds}%", label = stringResource(id = R.string.cloud_coverage))
            }
        }
    }
}

@Composable
fun WeatherDetail(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

