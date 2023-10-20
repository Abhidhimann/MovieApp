package com.example.movieapp.api

import com.example.movieapp.utils.Api
import com.example.movieapp.utils.RetroFitClientHelper

object MovieApiClient {

    val movieApi: MovieApiInterface =
        RetroFitClientHelper().getApiClient(Api.BASE_URL.getValue())
            .create(MovieApiInterface::class.java)

    val tvSeriesApi: TvApiInterface =  RetroFitClientHelper().getApiClient(Api.BASE_URL.getValue())
    .create(TvApiInterface::class.java)

}