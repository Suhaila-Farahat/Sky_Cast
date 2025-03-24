package com.example.skycast.data.repo

import com.example.skycast.data.remote.RetrofitClient
import com.example.skycast.data.remote.WeatherResponse

class WeatherRepository {
    private val api = RetrofitClient.apiService

    suspend fun getWeather(lat: Double, lon: Double, apiKey: String): WeatherResponse {
        return api.getCurrentWeather(lat, lon, apiKey)
    }
}
