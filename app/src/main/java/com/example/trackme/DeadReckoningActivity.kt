package com.example.trackme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.TextView

class DeadReckoningActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var inertiaSensor: Sensor? = null

    private var positionX: Float = 0.0f
    private var positionY: Float = 0.0f
    private var positionZ: Float = 0.0f

    private var velocityX: Float = 0.0f
    private var velocityY: Float = 0.0f
    private var velocityZ: Float = 0.0f

    private var accelerationX: Float = 0.0f
    private var accelerationY: Float = 0.0f
    private var accelerationZ: Float = 0.0f

    private var lastTimestampMs: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dead_reckoning)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        inertiaSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val currentTimestampMs = (event.timestamp / 1000000.0f).toLong()
        val deltaTimeMs = currentTimestampMs - lastTimestampMs
        if (BuildConfig.DEBUG && deltaTimeMs < 0.0f) {
            error("DeadReckoningActivity: deltaTimeMs < 0.0")
        }
        val deltaTimeS = deltaTimeMs / 1000.0f
        lastTimestampMs = currentTimestampMs

        accelerationX = event.values[1]
        accelerationY = event.values[0]
        accelerationZ = event.values[2]

        velocityX += accelerationX * deltaTimeS
        velocityY += accelerationY * deltaTimeS
        velocityZ += accelerationZ * deltaTimeS

        positionX += velocityX * deltaTimeS
        positionY += velocityY * deltaTimeS
        positionZ += velocityZ * deltaTimeS

        findViewById<TextView>(R.id.pos_N_val_m).text = getString(R.string.acc_x_val, positionX)
        findViewById<TextView>(R.id.pos_E_val_m).text = getString(R.string.acc_y_val, positionY)
        findViewById<TextView>(R.id.pos_H_val_m).text = getString(R.string.acc_z_val, positionZ)
    }

    override fun onResume() {
        super.onResume()
        inertiaSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            lastTimestampMs = SystemClock.elapsedRealtime()
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
