package com.example.skycast.workers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.skycast.R
import com.example.skycast.data.local.FavoriteDatabase
import com.example.skycast.data.local.LocalDataSource
import com.example.skycast.data.local.home.WeatherDatabase
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.data.remote.RemoteDataSource
import com.example.skycast.data.remote.RetrofitClient
import com.example.skycast.data.repo.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WeatherAlertWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val application = context.applicationContext as Application
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val weatherRepository = WeatherRepository(
        RemoteDataSource(RetrofitClient.apiService),
        LocalDataSource(
            FavoriteDatabase.getDatabase(application).favoriteLocationDao(),
            FavoriteDatabase.getDatabase(application).weatherAlertDao(),
            WeatherDatabase.getDatabase(application).weatherDao()

        ),
        context.getSharedPreferences("skycast_prefs", Context.MODE_PRIVATE)
    )

    private val apiKey = "c3b0faa25a8011e4d3ac4978f4b092f7"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val providedLatitude = inputData.getDouble("latitude", Double.MIN_VALUE)
            val providedLongitude = inputData.getDouble("longitude", Double.MIN_VALUE)

            val (latitude, longitude) = if (providedLatitude != Double.MIN_VALUE && providedLongitude != Double.MIN_VALUE) {
                Pair(providedLatitude, providedLongitude)
            } else {
                val location = getCurrentLocation()
                if (location != null) {
                    Pair(location.latitude, location.longitude)
                } else {
                    sendNotification("Weather Alert", "Unable to get your current location")
                    return@withContext Result.failure()
                }
            }

            Log.d("WeatherAlertWorker", "Fetching weather for coordinates: $latitude, $longitude")

            val weatherResponse = weatherRepository.getWeather(latitude, longitude, apiKey)

            processWeatherNotification(weatherResponse)

            Result.success()
        } catch (e: Exception) {
            Log.e("WeatherAlertWorker", "Error fetching weather data", e)
            sendNotification("Weather Alert", "Unable to fetch current weather data")
            Result.failure()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? = suspendCoroutine { continuation ->
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("WeatherAlertWorker", "Location permissions not granted")
            continuation.resume(null)
            return@suspendCoroutine
        }

        try {
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener { exception ->
                Log.e("WeatherAlertWorker", "Failed to get current location", exception)
                continuation.resume(null)
            }
        } catch (e: Exception) {
            Log.e("WeatherAlertWorker", "Exception getting location", e)
            continuation.resume(null)
        }
    }

    private fun processWeatherNotification(weatherResponse: WeatherResponse) {
        val locationName = weatherResponse.name

        val formattedLocation = locationName

        val temperature = weatherResponse.main.temp
        val formattedTemp = String.format("%.1f", temperature)
        val weatherDescription = weatherResponse.weather.firstOrNull()?.description?.capitalize() ?: "Unknown"

        val notificationMessage = "Current Temperature: ${formattedTemp}°C\n${weatherDescription}"

        sendNotification(
            "Weather Alert for $formattedLocation",
            notificationMessage
        )

        broadcastWeatherUpdate(weatherResponse, notificationMessage)
    }

    private fun broadcastWeatherUpdate(weatherResponse: WeatherResponse, notificationMessage: String) {
        val intent = Intent("com.example.breeze.WEATHER_UPDATE")
        intent.putExtra("weather_info", notificationMessage)
        intent.putExtra("temperature", weatherResponse.main.temp)
        intent.putExtra("location", weatherResponse.name)
        intent.putExtra("weather_id", weatherResponse.weather.firstOrNull()?.id ?: 0)
        applicationContext.sendBroadcast(intent)
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()
        val channelId = "weather_alerts"

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_alerts)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val soundUri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
        builder.setSound(soundUri)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for weather alerts"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, builder.build())
    }

    private fun String.capitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}


class MyWorkerFactory(private val repository: WeatherRepository) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParams: WorkerParameters
    ): ListenableWorker {
        return when (workerClassName) {
            WeatherAlertWorker::class.java.name -> WeatherAlertWorker(appContext, workerParams)
            else -> throw IllegalArgumentException("Unknown worker class: $workerClassName")
        }
    }
}
