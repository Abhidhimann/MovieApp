package com.example.movieapp.ui.activities

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.R
import com.example.movieapp.utils.RetryFunctionality
import com.example.movieapp.utils.NetworkConnection
import com.example.movieapp.utils.getClassTag
import kotlin.system.exitProcess

open class BaseActivity : AppCompatActivity() {

    private lateinit var networkConnection: NetworkConnection
    var activeFrames = mutableSetOf<RetryFunctionality>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(getClassTag(), "Activity created")

        networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) {
            Log.i(getClassTag(), "network value is $it")
            if (!it) {
                closingAlertDialogBox()
            }
        }
    }

    private fun closingAlertDialogBox() {
        val mBuilder = AlertDialog.Builder(this)
            .setTitle(R.string.alertDialogTitle)
            .setMessage(R.string.alertDialogMsg)
            .setPositiveButton(R.string.alertDialogPositionBtn, null)
            .setNegativeButton(R.string.alertDialogNegativeBtn, null)
            .setCancelable(false)
            .show()

        mBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            finish()
            exitProcess(0)
        }
        mBuilder.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            if (networkConnection.value==false){
                mBuilder.show()
                onPause()
            }else{
                for (activeFrame in activeFrames){
                    activeFrame.retryWhenInternetIsAvailable()
                }
                mBuilder.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(getClassTag(), "Network connection class getting unregister")
        networkConnection.unregisterNetworkCallback()
    }
}