package com.example.movieapp.api

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.RetroFitClientHelper
import com.example.movieapp.utils.getClassTag

object MovieApiClient {
    fun movieApi(): MovieApiInterface =
        RetroFitClientHelper().getApiClient(Api.BASE_URL.getValue())
            .create(MovieApiInterface::class.java)

//    val movieApi: MovieApiInterface =
//        RetroFitClientHelper().getApiClient(Api.BASE_URL.getValue())
//            .create(MovieApiInterface::class.java)

    fun tvSeriesApi(): TvApiInterface =
        RetroFitClientHelper().getApiClient(Api.BASE_URL.getValue())
            .create(TvApiInterface::class.java)

}