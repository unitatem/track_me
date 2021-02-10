package com.example.trackme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SensorsArrayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensors_array)
    }

    fun onClickAccelerometerButton(view: View) {
        val intent = Intent(this, AccelerometerActivity::class.java).apply {}
        startActivity(intent)
    }

    fun onClickWiFiButton(view: View) {
        val intent = Intent(this, WifiActivity::class.java).apply {}
        startActivity(intent)
    }

    fun onClickBtButton(view: View) {
        val intent = Intent(this, BtActivity::class.java).apply {}
        startActivity(intent)
    }
}