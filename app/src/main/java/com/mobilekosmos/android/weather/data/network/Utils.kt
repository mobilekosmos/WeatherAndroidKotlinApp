package com.mobilekosmos.android.weather.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.mobilekosmos.android.weather.MyApplication


class Utils {
    companion object {
        // TODO: replace deprecated code.
        fun isNetworkAvailable() : Boolean {
            val connectivityManager = MyApplication.getApplicationContext()
                ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }
}
