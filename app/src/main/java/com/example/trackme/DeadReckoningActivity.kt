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
import java.time.Instant

class DeadReckoningActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var inertiaSensor: Sensor? = null

    private var positionN: Float = 0.0f
    private var positionE: Float = 0.0f
    private var positionH: Float = 0.0f

    private var velocityN: Float = 0.0f
    private var velocityE: Float = 0.0f
    private var velocityH: Float = 0.0f

    private var accelerationN: Float = 0.0f
    private var accelerationE: Float = 0.0f
    private var accelerationH: Float = 0.0f

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

        accelerationN = event.values[1]
        accelerationE = event.values[0]
        accelerationH = event.values[2]

        velocityN += accelerationN * deltaTimeS
        velocityE += accelerationE * deltaTimeS
        velocityH += accelerationH * deltaTimeS

        positionN += velocityN * deltaTimeS
        positionE += velocityE * deltaTimeS
        positionH += velocityH * deltaTimeS

        findViewById<TextView>(R.id.pos_N_val_m).text = getString(R.string.acc_x_val, positionN)
        findViewById<TextView>(R.id.pos_E_val_m).text = getString(R.string.acc_x_val, positionE)
        findViewById<TextView>(R.id.pos_H_val_m).text = getString(R.string.acc_x_val, positionH)
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
