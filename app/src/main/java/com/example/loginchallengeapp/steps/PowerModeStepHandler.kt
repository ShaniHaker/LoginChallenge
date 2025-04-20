package com.example.loginchallengeapp.steps

import android.content.Context
import android.os.PowerManager
import android.widget.Toast

class PowerModeStepHandler(private val context: Context) {

    fun start(callback: (Boolean) -> Unit) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isPowerSaveMode = powerManager.isPowerSaveMode

        if (isPowerSaveMode) {
            callback(true)
        } else {
            Toast.makeText(context, "Power saving mode is OFF. Turn it on to proceed.", Toast.LENGTH_LONG).show()
            callback(false)
        }
    }
}
