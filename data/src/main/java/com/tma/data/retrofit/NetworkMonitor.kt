package com.tma.data.retrofit

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkMonitor(var appContext: Context) {
    fun isConnected(): Boolean {
        return (appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.let { connectivityManager ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.activeNetwork?.let { activeNetwork ->
                    connectivityManager.getNetworkCapabilities(activeNetwork)?.run {
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    } ?: false
                } ?: false
            } else {
                connectivityManager.activeNetworkInfo?.isConnected ?: false
            }
        } ?: false
    }
}