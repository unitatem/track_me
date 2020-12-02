package com.example.trackme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class AccelerometerActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accSensor: Sensor? = null
    private val logTag = "AccelerometerActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accelerometer)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        val chart = findViewById<View>(R.id.acc_chart) as LineChart

        val entries: MutableList<Entry> = mutableListOf()
        entries.add(Entry(0.0f, 0.0f))
        entries.add(Entry(1.0f, 1.0f))
        entries.add(Entry(2.0f, 0.0f))
        entries.add(Entry(3.0f, -1.0f))
        entries.add(Entry(4.0f, 0.0f))

        val dataSet = LineDataSet(entries, "sin")

        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        findViewById<TextView>(R.id.acc_value_x).text = getString(
            R.string.acc_x_val,
            event.values[0]
        )
        findViewById<TextView>(R.id.acc_value_y).text = getString(
            R.string.acc_x_val,
            event.values[1]
        )
        findViewById<TextView>(R.id.acc_value_z).text = getString(
            R.string.acc_x_val,
            event.values[2]
        )
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