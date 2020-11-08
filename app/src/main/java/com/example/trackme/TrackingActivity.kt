package com.example.trackme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class TrackingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)
    }

    fun onClickDeadReckoning(view: View) {
        val intent = Intent(this, DeadReckoningActivity::class.java).apply {}
        startActivity(intent)
    }
}