package com.example.movieapp.utils


fun Any.getClassTag(): String {
    return this::class.java.simpleName
}

fun Any.tempTag(): String {
    return "temp tag"
}