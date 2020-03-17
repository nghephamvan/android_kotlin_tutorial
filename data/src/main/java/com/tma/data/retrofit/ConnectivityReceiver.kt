package com.tma.data.retrofit

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build

/**
 * Internet status checking
 */
class ConnectivityReceiver(private var networkMonitor: NetworkMonitor) {

    var isNetworkConnected = true
    var connectivityReceiverListener: ConnectivityReceiverListener? = null

    /*
        Apps targeting Android 7.0 (API level 24) and higher do not receive
        CONNECTIVITY_ACTION broadcasts if they declare the broadcast receiver in their manifest.
        Apps will still receive CONNECTIVITY_ACTION broadcasts if they register their BroadcastReceiver
        with Context.registerReceiver() and that context is still valid.

        So, don't set this code in manifest.
        <receiver
        android:name="com.tma.data.retrofit.ConnectivityReceiver"
        android:enabled="true">
        <intent-filter>
        <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
        </intent-filter>
        </receiver>
    */
    private var broadcastReceiver: BroadcastReceiver? = null
    /*
        https://developer.android.com/reference/android/net/ConnectivityManager
        https://stackoverflow.com/questions/36421930/connectivitymanager-connectivity-action-deprecated/52718543
     */
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var connectivityManager: ConnectivityManager? = null

    init {
        isNetworkConnected = networkMonitor.isConnected()
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    isNetworkConnected = networkMonitor.isConnected()
                    connectivityReceiverListener?.onNetworkConnectionChanged(isNetworkConnected)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    isNetworkConnected = networkMonitor.isConnected()
                    connectivityReceiverListener?.onNetworkConnectionChanged(isNetworkConnected)
                }
            }
        } else {
            broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, p1: Intent?) {
                    isNetworkConnected = networkMonitor.isConnected()
                    connectivityReceiverListener?.onNetworkConnectionChanged(isNetworkConnected)
                }

            }
        }
    }

    fun registerReceiver(activity: Activity) {
        isNetworkConnected = networkMonitor.isConnected()
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            networkCallback?.also { netCallback ->
                connectivityManager =
                    (activity.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
                connectivityManager?.registerDefaultNetworkCallback(netCallback)
            }
        } else {
            broadcastReceiver?.let { broad ->
                activity.registerReceiver(
                    broad,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }
    }

    fun unregisterReceiver(activity: Activity) {
        networkCallback?.let {
            connectivityManager?.unregisterNetworkCallback(it)
        } ?: broadcastReceiver?.let {
            activity.unregisterReceiver(it)
        }
        connectivityReceiverListener = null
    }

}

interface ConnectivityReceiverListener {
    fun onNetworkConnectionChanged(isConnected: Boolean)
}