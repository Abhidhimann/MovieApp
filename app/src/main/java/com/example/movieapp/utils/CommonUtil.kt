package com.example.movieapp.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object CommonUtil {

    private var currentToast: Toast? = null

    // Toast can outlive activity
    fun shortToast(context: Context, text: String) {
        if (currentToast != null) return
        currentToast = Toast.makeText(context.applicationContext, text, Toast.LENGTH_SHORT)
        currentToast?.show()
        Handler(Looper.getMainLooper()).postDelayed({ currentToast = null }, 3000)
    }

    fun longToast(context: Context, text: String) {
        if (currentToast != null) return
        currentToast = Toast.makeText(context.applicationContext, text, Toast.LENGTH_LONG)
        currentToast?.show()
        Handler(Looper.getMainLooper()).postDelayed({ currentToast = null }, 5000)
    }
}