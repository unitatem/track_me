package com.example.trackme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AccelerometerActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accSensor: Sensor? = null
    private val logTag = "AccelerometerActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accelerometer)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val unit_m_s2 = "m/s^2"
        findViewById<TextView>(R.id.acc_value_x).text = getString(R.string.acc_x_val, event.values[0])
        findViewById<TextView>(R.id.acc_value_y).text = getString(R.string.acc_x_val, event.values[1])
        findViewById<TextView>(R.id.acc_value_z).text = getString(R.string.acc_x_val, event.values[2])
    }

    override fun onResume() {
        Log.v(logTag, "onResume() >")
        super.onResume()
        accSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        Log.v(logTag, "onPause() >")
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}