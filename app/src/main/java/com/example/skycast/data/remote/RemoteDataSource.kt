package com.example.skycast.data.remote

import com.example.skycast.data.model.ForecastResponse
import com.example.skycast.data.model.WeatherResponse


class RemoteDataSource(private val apiService: WeatherApiService) {

    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String): WeatherResponse {
        return apiService.getCurrentWeather(lat, lon, apiKey)
    }

    suspend fun getHourlyForecast(lat: Double, lon: Double, apiKey: String): ForecastResponse {
        return apiService.getHourlyForecast(lat, lon, apiKey)
    }
}
