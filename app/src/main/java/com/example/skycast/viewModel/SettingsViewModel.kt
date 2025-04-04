package com.example.skycast.viewModel

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.repo.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import android.content.res.Configuration

class SettingsViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _locationMode = MutableStateFlow(repository.getLocationMode())
    val locationMode = _locationMode.asStateFlow()

    private val _temperatureUnit = MutableStateFlow(repository.getTemperatureUnit())
    val temperatureUnit = _temperatureUnit.asStateFlow()

    private val _windSpeedUnit = MutableStateFlow(repository.getWindSpeedUnit())
    val windSpeedUnit = _windSpeedUnit.asStateFlow()

    private val _language = MutableStateFlow(repository.getLanguage())
    val language = _language.asStateFlow()

    fun setLocationMode(mode: String) {
        viewModelScope.launch {
            repository.setLocationMode(mode)
            _locationMode.value = mode
        }
    }

    fun setTemperatureUnit(unit: String) {
        viewModelScope.launch {
            repository.setTemperatureUnit(unit)
            _temperatureUnit.value = unit
            // Automatically update wind speed unit when temperature changes
            updateWindSpeedUnitBasedOnTemperatureUnit(unit)
        }
    }

    fun setWindSpeedUnit(unit: String) {
        viewModelScope.launch {
            repository.setWindSpeedUnit(unit)
            _windSpeedUnit.value = unit
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            repository.setLanguage(lang)
            _language.value = lang
            updateAppLanguage(lang)
        }
    }

    private fun updateAppLanguage(lang: String) {
        val locale = when (lang) {
            "Arabic" -> Locale("ar")
            else -> Locale("en")
        }

        Locale.setDefault(locale)

        val config = Configuration(Resources.getSystem().configuration)
        config.setLocale(locale)
        Resources.getSystem().updateConfiguration(config, Resources.getSystem().displayMetrics)
    }

    private fun updateWindSpeedUnitBasedOnTemperatureUnit(temperatureUnit: String) {
        // Example logic for auto-changing wind speed unit based on temperature unit
        val newWindSpeedUnit = if (temperatureUnit == "Celsius") "meter/sec" else "miles/hour"
        repository.setWindSpeedUnit(newWindSpeedUnit)
        _windSpeedUnit.value = newWindSpeedUnit
    }
}

class SettingsViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}