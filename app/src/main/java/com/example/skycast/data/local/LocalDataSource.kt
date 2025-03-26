package com.example.skycast.data.local


class LocalDataSource(private val favoriteLocationDao: FavoriteLocationDao) {

    suspend fun addFavoriteLocation(location: FavoriteLocationEntity) {
        favoriteLocationDao.insertFavoriteLocation(location)
    }

    suspend fun removeFavoriteLocation(location: FavoriteLocationEntity) {
        favoriteLocationDao.deleteFavoriteLocation(location)
    }

    fun getFavoriteLocations() = favoriteLocationDao.getAllFavoriteLocations()
}
