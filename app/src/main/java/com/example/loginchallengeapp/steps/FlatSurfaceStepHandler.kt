package com.example.loginchallengeapp.steps

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast

class FlatSurfaceStepHandler(private val context: Context) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var callback: ((Boolean) -> Unit)? = null

    fun start(callback: (Boolean) -> Unit) {
        this.callback = callback
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            Toast.makeText(context, "Accelerometer not available", Toast.LENGTH_SHORT).show()
            callback(false)
            return
        }

        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val isFlat = Math.abs(x) < 2 && Math.abs(y) < 2 && z > 9.0 && z < 10.5

        if (isFlat) {
            Toast.makeText(context, "Device is lying flat – access granted!", Toast.LENGTH_SHORT).show()
            stop()
            callback?.invoke(true)
        } else {
            Toast.makeText(context, "Lay the device flat on a surface to continue.", Toast.LENGTH_SHORT).show()
            stop()
            callback?.invoke(false)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not using this
    }

    private fun stop() {
        sensorManager?.unregisterListener(this)
    }
}
