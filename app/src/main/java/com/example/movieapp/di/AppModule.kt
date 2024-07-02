package com.example.movieapp.di

import android.app.Application
import androidx.room.Room
import com.example.movieapp.BuildConfig
import com.example.movieapp.data.local.dao.SavedItemDao
import com.example.movieapp.data.local.database.AppDatabase
import com.example.movieapp.data.remote.network.MovieApiService
import com.example.movieapp.data.remote.network.TvApiService
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.RetroFitClientHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesAppDatabase(application: Application) = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        BuildConfig.DB_NAME
    ).build()

    @Provides
    @Singleton
    fun providesMovieApi(): MovieApiService {
       return RetroFitClientHelper().getApiClient(Api.BASE_URL.getValue())
            .create(MovieApiService::class.java)
        // instead of RetroFitClientHelper() can also use singleton and provides
        // but I like RetroFitClientHelper class
    }

    @Provides
    @Singleton
    fun providesTvSeriesApi(): TvApiService {
        return RetroFitClientHelper().getApiClient(Api.BASE_URL.getValue())
            .create(TvApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesSavedItemDao(appDatabase: AppDatabase): SavedItemDao = appDatabase.savedItemDao()

}

/*
    Which to Use
    Prefer constructor injection with @Inject when:

    The dependencies are straightforward and can be directly injected.
    You want to minimize boilerplate code.
    You are following a clean architecture and want to keep things simple.
    Use @Provides methods in a module when:

    You need to provide instances of third-party libraries, interfaces, or classes where constructor injection isn't feasible.
    The initialization logic is complex and needs explicit control.
    You want to make the dependency graph more explicit for better readability.
 */