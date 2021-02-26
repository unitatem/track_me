package com.example.trackme.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class AccelerometerPublisher private constructor(private val mContext: Context): SensorPublisher(), SensorEventListener {
    private val TAG: String = AccelerometerPublisher::class.qualifiedName.toString()

    private val sensorManager: SensorManager by lazy { mContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val inertiaSensor: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) }

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
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun lastSubscriberUnregistered() {
        Log.d(TAG, "lastSubscriberUnregistered >")
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val vector3d = Vector3d(
            event.values[1].toDouble(), event.values[0].toDouble(), event.values[2].toDouble()
        )

        notifySubscribers(vector3d)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.v(TAG, "onAccuracyChanged >")
    }
}