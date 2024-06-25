package com.example.movieapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.movieapp.BuildConfig
import com.example.movieapp.data.local.entity.SavedItemEntity
import com.example.movieapp.data.local.dao.SavedItemDao


@Database(entities = [SavedItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun savedItemDao(): SavedItemDao


    // later move it to di/modules
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    BuildConfig.DB_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}