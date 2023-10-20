package com.example.movieapp.utils

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetroFitClientHelper {
    private var retrofit: Retrofit? = null

    fun getApiClient(baseUrl: String): Retrofit {
        val gson = GsonBuilder().setLenient().create()

        val okHttpClient = OkHttpClient.Builder().readTimeout(100, TimeUnit.SECONDS)
            .connectTimeout(100, TimeUnit.SECONDS).build()


        if (retrofit == null) {
            retrofit = Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }

        return retrofit ?: throw IllegalStateException("Retrofit instance not initialized")
    }
}

//val client = OkHttpClient.Builder()
//    .addInterceptor { chain: Interceptor.Chain ->
//        val original: Request = chain.request()
//        val originalHttpUrl: HttpUrl = original.url()
//        val url = originalHttpUrl.newBuilder()
//            .addQueryParameter("api_key", "your_api_key")
//            .build()
//        val requestBuilder: Request.Builder = original.newBuilder()
//            .url(url)
//        val request: Request = requestBuilder.build()
//        chain.proceed(request)
//    }
//    .build()