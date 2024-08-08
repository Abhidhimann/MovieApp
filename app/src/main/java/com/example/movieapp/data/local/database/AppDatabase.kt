package com.example.movieapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movieapp.data.local.entity.SavedItemEntity
import com.example.movieapp.data.local.dao.SavedItemDao


@Database(entities = [SavedItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun savedItemDao(): SavedItemDao
}