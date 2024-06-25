package com.example.movieapp

import android.app.Application
import com.example.movieapp.data.local.database.AppDatabase


class MyApp: Application() {
    val dataBase by lazy { AppDatabase.getDatabase(this) }
}