package com.example.skycast.data.repo

import com.example.skycast.data.remote.RetrofitClient
import com.example.skycast.data.remote.WeatherResponse

class WeatherRepository {
    private val api = RetrofitClient.apiService

    suspend fun getWeather(city: String, apiKey: String): WeatherResponse {
        return api.getCurrentWeather(city, apiKey)
    }
}

