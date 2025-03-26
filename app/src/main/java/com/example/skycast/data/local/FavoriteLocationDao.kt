package com.example.skycast.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteLocation(location: FavoriteLocationEntity)

    @Delete
    suspend fun deleteFavoriteLocation(location: FavoriteLocationEntity)

    @Query("SELECT * FROM favorite_locations")
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocationEntity>>
}
