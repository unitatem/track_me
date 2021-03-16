package com.example.trackme.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

data class AccelerometerMeasure(val measure: Vector3f, val timestampMs: Long)

class AccelerometerPublisher private constructor(private val mContext: Context): SensorPublisher(), SensorEventListener {
    private val TAG: String = AccelerometerPublisher::class.qualifiedName.toString()

    private val sensorManager: SensorManager by lazy { mContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val inertiaSensor: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) }

    private val mCalibration: AccelerometerCalibration by lazy { AccelerometerCalibration(500) }

    init {
        Log.i(TAG, "init >")
    }

    companion object {
        private var mInstance: AccelerometerPublisher? = null

        fun getInstance(context: Context): AccelerometerPublisher {
            if (mInstance == null) {
                mInstance = AccelerometerPublisher(context)
            }
            return mInstance as AccelerometerPublisher
        }
    }

    override fun firstSubscriberRegistered() {
        Log.d(TAG, "firstSubscriberRegistered >")
        inertiaSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun lastSubscriberUnregistered() {
        Log.d(TAG, "lastSubscriberUnregistered >")
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val measure = Vector3f(event.values[1], event.values[0], event.values[2])

        if (mCalibration.needsCalibration()) {
            mCalibration.updateCalibration(measure)
        } else {
            val timestampMs: Long = (event.timestamp / 1000000.0f).toLong()

            val staticOffset = mCalibration.getStaticOffset()
            measure.x -= staticOffset.x
            measure.y -= staticOffset.y
            measure.z -= staticOffset.z

            val data = AccelerometerMeasure(measure, timestampMs)
            notifySubscribers(data)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.v(TAG, "onAccuracyChanged >")
    }
}