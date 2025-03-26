package com.example.skycast.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_locations")
data class FavoriteLocationEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double
)
