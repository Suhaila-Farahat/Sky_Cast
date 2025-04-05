package com.example.skycast.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.skycast.data.local.alert.WeatherAlert
import com.example.skycast.data.repo.WeatherRepository
import com.example.skycast.workers.WeatherAlertWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AlertViewModel(
    application: Application,
    private val repository: WeatherRepository
) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val _alerts = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val alerts: StateFlow<List<WeatherAlert>> = _alerts

    init {
        viewModelScope.launch {
            repository.getActiveWeatherAlerts().collect {
                _alerts.value = it
            }
        }
    }

    fun addAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repository.addWeatherAlert(alert)
            scheduleAlertWork(alert)
        }
    }

    fun removeAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repository.removeWeatherAlert(alert)
        }
    }

    fun scheduleAlertWork(alert: WeatherAlert) {
        val delay = alert.time - System.currentTimeMillis()

        if (delay > 0) {
            val workRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("alert_id" to alert.id))
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        } else {
            Log.e("AlertViewModel", "Alert time has already passed!")
        }
    }

}

class AlertViewModelFactory(
    private val application: Application,
    private val weatherRepository: WeatherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            return AlertViewModel(application, weatherRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
