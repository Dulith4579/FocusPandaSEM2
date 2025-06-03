package com.example.focuspanda.Model

import android.content.*
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Data class representing battery state
 */
data class BatteryStatus(
    val level: Int = 0,
    val isPowerSaveMode: Boolean = false
)

/**
 * Network monitoring helper
 */
object NetworkMonitor {
    fun networkState(connectivityManager: ConnectivityManager): Flow<Boolean> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                private val activeNetworks = mutableSetOf<Network>()

                override fun onAvailable(network: Network) {
                    activeNetworks.add(network)
                    trySend(true)
                }

                override fun onLost(network: Network) {
                    activeNetworks.remove(network)
                    trySend(activeNetworks.isNotEmpty())
                }
            }

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            // Always register on main thread
            Handler(Looper.getMainLooper()).post {
                connectivityManager.registerNetworkCallback(request, callback)
            }

            // Initial state check (simplified)
            trySend(connectivityManager.activeNetwork != null)

            awaitClose {
                Handler(Looper.getMainLooper()).post {
                    try {
                        connectivityManager.unregisterNetworkCallback(callback)
                    } catch (e: IllegalArgumentException) {
                        // Callback was not registered
                    }
                }
            }
        }
    }
}

/**
 * Battery monitoring helper
 */
object BatteryMonitor {
    fun batteryState(context: Context): Flow<BatteryStatus> {
        return callbackFlow {
            // Get initial state
            trySend(getCurrentBatteryStatus(context))

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    trySend(getCurrentBatteryStatus(context))
                }
            }

            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_BATTERY_CHANGED)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
                }
            }

            context.registerReceiver(receiver, filter)

            awaitClose {
                try {
                    context.unregisterReceiver(receiver)
                } catch (e: IllegalArgumentException) {
                    // Receiver was not registered
                }
            }
        }
    }

    private fun getCurrentBatteryStatus(context: Context): BatteryStatus {
        val batteryIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: return BatteryStatus()

        val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = if (scale > 0) (level * 100 / scale) else 0

        val isPowerSaveMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            (context.getSystemService(Context.POWER_SERVICE) as? PowerManager)
                ?.isPowerSaveMode ?: false
        } else false

        return BatteryStatus(batteryPct, isPowerSaveMode)
    }
}