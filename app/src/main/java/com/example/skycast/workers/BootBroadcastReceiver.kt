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
import com.example.skycast.data.local.home.WeatherDatabase
import com.example.skycast.data.remote.RemoteDataSource
import com.example.skycast.data.remote.RetrofitClient
import com.example.skycast.data.repo.WeatherRepository
import com.example.skycast.workers.WeatherAlertWorker
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repository = WeatherRepository(
                RemoteDataSource(RetrofitClient.apiService),
                LocalDataSource(
                    FavoriteDatabase.getDatabase(context).favoriteLocationDao(),
                    FavoriteDatabase.getDatabase(context).weatherAlertDao(),
                    WeatherDatabase.getDatabase(context).weatherDao()

                ),
                context.getSharedPreferences("skycast_prefs", Context.MODE_PRIVATE)
            )

            runBlocking {
                val alerts = repository.getActiveWeatherAlerts().first()

                alerts.forEach { alert ->
                    val delay = alert.time - System.currentTimeMillis()

                    if (delay > 0) {
                        val workRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .setInputData(workDataOf("alert_id" to alert.id))
                            .build()

                        WorkManager.getInstance(context).enqueue(workRequest)
                    }
                }
            }

            Toast.makeText(context, "Alerts rescheduled after reboot", Toast.LENGTH_SHORT).show()
        }
    }
}
