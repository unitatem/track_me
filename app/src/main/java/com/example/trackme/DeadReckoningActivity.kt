package com.example.trackme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.TextView
import com.example.trackme.sensor.AccelerometerMeasure
import com.example.trackme.sensor.AccelerometerPublisher
import com.example.trackme.sensor.SensorSubscriber
import com.example.trackme.sensor.Vector3f

class DeadReckoningActivity : AppCompatActivity() {
    private val TAG: String = DeadReckoningActivity::class.qualifiedName.toString()

    private val mAccelerometer by lazy { AccelerometerPublisher.getInstance(this) }

    private var position = Vector3f()
    private var velocity = Vector3f()
    private val acceleration = Vector3f()
    private var lastTimestampMs: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate >")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dead_reckoning)
    }

    private val mAccelerometerSubscriber = object: SensorSubscriber() {
        override fun update(data: AccelerometerMeasure) {
            val currentTimestampMs = data.timestampMs
            val deltaTimeMs = currentTimestampMs - lastTimestampMs
            if (BuildConfig.DEBUG && deltaTimeMs < 0.0f) {
                error("DeadReckoningActivity: deltaTimeMs < 0.0")
            }
            val deltaTimeS = deltaTimeMs / 1000.0f
            lastTimestampMs = currentTimestampMs

            acceleration.x = data.measure.x
            acceleration.y = data.measure.y
            acceleration.z = data.measure.z

            velocity.x += acceleration.x * deltaTimeS
            velocity.y += acceleration.y * deltaTimeS
            velocity.z += acceleration.z * deltaTimeS

            position.x += velocity.x * deltaTimeS
            position.y += velocity.y * deltaTimeS
            position.z += velocity.z * deltaTimeS

            findViewById<TextView>(R.id.pos_N_val_m).text = getString(R.string.acc_x_val, position.x)
            findViewById<TextView>(R.id.pos_E_val_m).text = getString(R.string.acc_y_val, position.y)
            findViewById<TextView>(R.id.pos_H_val_m).text = getString(R.string.acc_z_val, position.z)
        }
    }

    override fun onResume() {
        Log.i(TAG, "onResume >")
        super.onResume()

        lastTimestampMs = SystemClock.elapsedRealtime()
        mAccelerometer.subscribe(mAccelerometerSubscriber)
    }

    override fun onPause() {
        Log.i(TAG, "onPause >")
        super.onPause()

        mAccelerometer.unsubscribe(mAccelerometerSubscriber)
    }
}
