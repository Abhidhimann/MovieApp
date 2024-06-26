package com.example.movieapp.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.movieapp.utils.getClassTag

import android.os.Handler
import android.os.Looper

class NetworkConnection(context: Context) : LiveData<Boolean>() {

    private var hasInternet: Boolean

    init {
        hasInternet = false
        postingNoInternet()
    }

    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun onActive() {
        super.onActive()
        connectivityManager.registerDefaultNetworkCallback(networkCallback())
    }

    fun unregisterNetworkCallback(){
        Log.i(getClassTag(), "Network connection class unregistered")
        connectivityManager.registerDefaultNetworkCallback(networkCallback())
    }

//    override fun onInactive() {
//        super.onInactive()
//        connectivityManager.unregisterNetworkCallback(networkCallback())
//    }


    private fun networkCallback(): ConnectivityManager.NetworkCallback {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                hasInternet = true
                postValue(hasInternet)
            }

            // Method provide OnUnAvailable in not working, so using this approach
            // getting network connection state after 1 sec ( with covering wifi switch on case
            // when data is on)
            override fun onLost(network: Network) {
                super.onLost(network)
                hasInternet = false
                postingNoInternet()
            }
        }

        return callback
    }

    // this one delay
    fun postingNoInternet(){
        Handler(Looper.getMainLooper()).postDelayed({
            postValue(hasInternet)
        }, 1000)
    }

}