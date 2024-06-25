package com.example.movieapp.data.remote.network

import com.example.movieapp.utils.Api
import com.example.movieapp.utils.RetroFitClientHelper

// move it to di/module later
object MovieApiClient {
    fun movieApi(): MovieApiService =
        RetroFitClientHelper().getApiClient(Api.BASE_URL.getValue())
            .create(MovieApiService::class.java)

//    val movieApi: MovieApiInterface =
//        RetroFitClientHelper().getApiClient(Api.BASE_URL.getValue())
//            .create(MovieApiInterface::class.java)

    fun tvSeriesApi(): TvApiService =
        RetroFitClientHelper().getApiClient(Api.BASE_URL.getValue())
            .create(TvApiService::class.java)

}