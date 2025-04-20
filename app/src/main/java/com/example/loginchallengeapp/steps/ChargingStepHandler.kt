package com.example.loginchallengeapp.steps

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.Toast

class ChargingStepHandler(private val context: Context) {

    fun start(callback: (Boolean) -> Unit) {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)

        val pluggedStatus = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

        val isCharging = when (pluggedStatus) {
            BatteryManager.BATTERY_PLUGGED_AC,
            BatteryManager.BATTERY_PLUGGED_USB,
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> true
            else -> false
        }

        if (isCharging) {
            Toast.makeText(context, "Device is charging. Access granted!", Toast.LENGTH_SHORT).show()
            callback(true)
        } else {
            Toast.makeText(context, "Device is not charging. Please plug it in to continue.", Toast.LENGTH_LONG).show()
            callback(false)
        }
    }
}
