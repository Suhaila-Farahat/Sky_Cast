package com.example.skycast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.skycast.data.local.FavoriteDatabase
import com.example.skycast.data.local.LocalDataSource
import com.example.skycast.data.local.alert.WeatherAlert
import com.example.skycast.data.remote.RemoteDataSource
import com.example.skycast.data.remote.RetrofitClient
import com.example.skycast.data.repo.WeatherRepository
import com.example.skycast.workers.WeatherAlertWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Initialize repository
            val repository = WeatherRepository(
                RemoteDataSource(RetrofitClient.apiService),
                LocalDataSource(
                    FavoriteDatabase.getDatabase(context).favoriteLocationDao(),
                    FavoriteDatabase.getDatabase(context).weatherAlertDao()
                ),
                context.getSharedPreferences("skycast_prefs", Context.MODE_PRIVATE)
            )

            // Fetch all active weather alerts from the repository
            runBlocking {
                val alerts = repository.getActiveWeatherAlerts().first() // Collect the first value from the flow

                // Iterate over each alert and schedule work
                alerts.forEach { alert ->
                    // Calculate the delay based on alert time
                    val delay = alert.time - System.currentTimeMillis()

                    if (delay > 0) {
                        // Create a work request to trigger the alert after the calculated delay
                        val workRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .setInputData(workDataOf("alert_id" to alert.id)) // Use 'id' as input
                            .build()

                        // Enqueue the work request to handle the alert
                        WorkManager.getInstance(context).enqueue(workRequest)
                    }
                }
            }

            // Show a toast indicating that alerts have been rescheduled
            Toast.makeText(context, "Alerts rescheduled after reboot", Toast.LENGTH_SHORT).show()
        }
    }
}
