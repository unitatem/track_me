package com.example.trackme

import android.content.Context
import android.content.Intent
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
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.PI
import kotlin.math.sin


class AccelerometerActivity : AppCompatActivity(), SensorEventListener {
    private val TAG = "AccelerometerActivity"

    private lateinit var sensorManager: SensorManager
    private var accSensor: Sensor? = null

    private var chart: LineChart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accelerometer)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        chart = findViewById<View>(R.id.acc_chart) as LineChart

        val entries: MutableList<Entry> = mutableListOf()
        val dataSet = LineDataSet(entries, "x")
        val lineData = LineData(dataSet)
        chart!!.data = lineData
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

        chart!!.data.getDataSetByLabel("x", false).addEntry(Entry(chart!!.data.entryCount.toFloat(), event.values[0]))

        chart!!.data.notifyDataChanged()
        chart!!.notifyDataSetChanged()

        chart!!.setVisibleXRangeMaximum(50.0f);
        chart!!.moveViewToX(chart!!.data.entryCount.toFloat());
    }

    // TODO remove later
    fun onClickButton(view: View) {
        chart!!.data.getDataSetByLabel("x", false).addEntry(Entry(chart!!.data.entryCount.toFloat(), 1.0f))

        chart!!.data.notifyDataChanged()
        chart!!.notifyDataSetChanged()
        chart!!.invalidate();
    }

    override fun onResume() {
        Log.v(TAG, "onResume() >")
        super.onResume()
        accSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onPause() {
        Log.v(TAG, "onPause() >")
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}