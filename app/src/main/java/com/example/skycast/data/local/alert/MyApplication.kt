package com.example.skycast.data.local.alert

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import com.example.skycast.data.local.FavoriteDatabase
import com.example.skycast.data.local.LocalDataSource
import com.example.skycast.data.local.home.WeatherDatabase
import com.example.skycast.data.remote.RemoteDataSource
import com.example.skycast.data.remote.RetrofitClient
import com.example.skycast.data.repo.WeatherRepository
import com.example.skycast.workers.MyWorkerFactory

class MyApplication : Application(), Configuration.Provider {

    private lateinit var workerFactory: MyWorkerFactory

    override fun onCreate() {
        super.onCreate()

        val remoteDataSource = RemoteDataSource(RetrofitClient.apiService)
        val localDataSource = LocalDataSource(
            FavoriteDatabase.getDatabase(applicationContext).favoriteLocationDao(),
            FavoriteDatabase.getDatabase(applicationContext).weatherAlertDao(),
            WeatherDatabase.getDatabase(applicationContext).weatherDao()

        )
        val sharedPreferences = getSharedPreferences("skycast_prefs", MODE_PRIVATE)

        val repository = WeatherRepository(remoteDataSource, localDataSource, sharedPreferences)

        workerFactory = MyWorkerFactory(repository)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weather_alerts", "Weather Alerts", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather Alert Notifications"
                enableVibration(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(workerFactory).build()
    }
}
