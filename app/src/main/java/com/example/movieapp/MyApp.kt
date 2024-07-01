package com.example.movieapp

import android.app.Application
import com.example.movieapp.data.local.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApp: Application()