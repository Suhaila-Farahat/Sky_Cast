package com.example.skycast.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.skycast.data.local.alert.WeatherAlert
import com.example.skycast.data.local.alert.WeatherAlertDao
import com.example.skycast.data.local.fav.FavoriteLocationDao
import com.example.skycast.data.local.fav.FavoriteLocationEntity

@Database(entities = [FavoriteLocationEntity::class, WeatherAlert::class], version = 2, exportSchema = false)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteLocationDao(): FavoriteLocationDao
    abstract fun weatherAlertDao(): WeatherAlertDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteDatabase? = null

        fun getDatabase(context: Context): FavoriteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteDatabase::class.java,
                    "favorite_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
