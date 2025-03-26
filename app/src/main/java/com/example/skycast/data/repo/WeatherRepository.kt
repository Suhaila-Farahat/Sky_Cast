package com.example.skycast.repository

import com.example.skycast.data.model.ForecastResponse
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.data.remote.RemoteDataSource


class WeatherRepository(private val remoteDataSource: RemoteDataSource) {

    suspend fun getWeather(lat: Double, lon: Double, apiKey: String): WeatherResponse {
        return remoteDataSource.getCurrentWeather(lat, lon, apiKey)
    }

    suspend fun getHourlyForecast(lat: Double, lon: Double, apiKey: String): ForecastResponse {
        return remoteDataSource.getHourlyForecast(lat, lon, apiKey)
    }
}
