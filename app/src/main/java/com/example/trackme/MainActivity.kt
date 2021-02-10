package com.example.trackme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickSensorsArrayButton(view: View) {
        val intent = Intent(this, SensorsArrayActivity::class.java).apply {}
        startActivity(intent)
    }

    fun onClickTrackingButton(view: View) {
        val intent = Intent(this, TrackingActivity::class.java).apply {}
        startActivity(intent)
    }
}