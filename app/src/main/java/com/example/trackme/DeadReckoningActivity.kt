package com.example.trackme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.TextView
import com.example.trackme.processing.DeadReckoningEngine
import com.example.trackme.sensor.AccelerometerMeasure
import com.example.trackme.sensor.AccelerometerPublisher
import com.example.trackme.sensor.SensorSubscriber
import com.example.trackme.sensor.Vector3f

class DeadReckoningActivity : AppCompatActivity() {
    private val TAG: String = DeadReckoningActivity::class.qualifiedName.toString()

    private val mAccelerometer by lazy { AccelerometerPublisher.getInstance(this) }

    private val mDeadReckoningEngine by lazy { DeadReckoningEngine() }
    private var lastTimestampMs: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate >")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dead_reckoning)
    }

    private val mAccelerometerSubscriber = object: SensorSubscriber() {
        override fun update(data: AccelerometerMeasure) {
            val deltaTimeS = calculateElapsedTime(data.timestampMs)

            mDeadReckoningEngine.calculateStep(deltaTimeS,
                Vector3f(data.measure.x, data.measure.y, data.measure.z)
            )

            findViewById<TextView>(R.id.pos_N_val_m).text = getString(R.string.acc_x_val, mDeadReckoningEngine.getPositionX())
            findViewById<TextView>(R.id.pos_E_val_m).text = getString(R.string.acc_y_val, mDeadReckoningEngine.getPositionY())
            findViewById<TextView>(R.id.pos_H_val_m).text = getString(R.string.acc_z_val, mDeadReckoningEngine.getPositionZ())
        }
    }

    private fun calculateElapsedTime(currentTimestampMs: Long): Float {
        val deltaTimeMs = currentTimestampMs - lastTimestampMs
        if (BuildConfig.DEBUG && deltaTimeMs < 0.0f) {
            error("DeadReckoningActivity: deltaTimeMs < 0.0")
        }
        lastTimestampMs = currentTimestampMs

        val deltaTimeS = deltaTimeMs / 1000.0f
        return deltaTimeS
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
