package com.example.skycast.data.repo

import com.example.skycast.data.local.FavoriteLocationEntity
import com.example.skycast.data.local.LocalDataSource
import com.example.skycast.data.model.ForecastResponse
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    // Remote API Calls
    suspend fun getWeather(lat: Double, lon: Double, apiKey: String): WeatherResponse {
        return remoteDataSource.getCurrentWeather(lat, lon, apiKey)
    }

    suspend fun getHourlyForecast(lat: Double, lon: Double, apiKey: String): ForecastResponse {
        return remoteDataSource.getHourlyForecast(lat, lon, apiKey)
    }

    // Favorite Location Operations
    suspend fun addFavoriteLocation(location: FavoriteLocationEntity) {
        localDataSource.addFavoriteLocation(location)
    }

    suspend fun removeFavoriteLocation(location: FavoriteLocationEntity) {
        localDataSource.removeFavoriteLocation(location)
    }

    fun getFavoriteLocations(): Flow<List<FavoriteLocationEntity>> {
        return localDataSource.getFavoriteLocations()
    }
}
