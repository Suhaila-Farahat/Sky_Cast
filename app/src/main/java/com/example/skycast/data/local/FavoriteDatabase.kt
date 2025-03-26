package com.example.skycast.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteLocationEntity::class], version = 1, exportSchema = false)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteLocationDao(): FavoriteLocationDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteDatabase? = null

        fun getDatabase(context: Context): FavoriteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteDatabase::class.java,
                    "favorite_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
