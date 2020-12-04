package com.example.trackme

import android.content.Context
import android.graphics.Color
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


class AccelerometerActivity : AppCompatActivity(), SensorEventListener {
    private val TAG = "AccelerometerActivity"

    private lateinit var mSensorManager: SensorManager
    private var mAccSensor: Sensor? = null

    private lateinit var mChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accelerometer)

        initializeSensor()
        initializeChart()
    }

    private fun initializeSensor() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun initializeChart() {
        val lineData = LineData()
        lineData.addDataSet(createDataSet("x", Color.RED))
        lineData.addDataSet(createDataSet("y", Color.GREEN))
        lineData.addDataSet(createDataSet("z", Color.BLUE))

        mChart = findViewById<View>(R.id.acc_chart) as LineChart
        mChart.data = lineData

        val rightAxis: YAxis = mChart.axisRight
        rightAxis.isEnabled = false
    }

    private fun createDataSet(label: String, color: Int): LineDataSet {
        val entries: MutableList<Entry> = mutableListOf()
        val dataSet = LineDataSet(entries, label)

        dataSet.setDrawValues(false)
        dataSet.color = color
        dataSet.setCircleColor(color)

        return dataSet
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        showSensorMeasurements(event)
        displaySensorMeasurements(event)
    }

    private fun showSensorMeasurements(event: SensorEvent) {
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

    private fun displaySensorMeasurements(event: SensorEvent) {
        val entriesCnt = mChart.data.entryCount.toFloat()
        mChart.data.getDataSetByLabel("x", false)
            .addEntry(Entry(entriesCnt, event.values[0]))
        mChart.data.getDataSetByLabel("y", false)
            .addEntry(Entry(entriesCnt, event.values[1]))
        mChart.data.getDataSetByLabel("z", false)
            .addEntry(Entry(entriesCnt, event.values[2]))

        mChart.data.notifyDataChanged()
        mChart.notifyDataSetChanged()

        mChart.setVisibleXRangeMaximum(50.0f);
        mChart.moveViewToX(entriesCnt);
    }

    override fun onResume() {
        Log.v(TAG, "onResume() >")
        super.onResume()
        mAccSensor?.also { sensor ->
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        Log.v(TAG, "onPause() >")
        super.onPause()
        mSensorManager.unregisterListener(this)
    }
}
