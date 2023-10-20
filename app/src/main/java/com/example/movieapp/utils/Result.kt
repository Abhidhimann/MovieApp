package com.example.movieapp.utils

sealed class Result<out T>(val data: T? = null, val exception: Exception? = null) {
    class Success<out T>(data: T) : Result<T>(data)
    class Error(exception: Exception) : Result<Nothing>(exception = exception)
}
