package com.example.trackme

import android.os.Bundle
import android.util.Log


class WifiActivity : WifiStrategy() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_wifi)
        super.onCreate(savedInstanceState)
    }

    override fun scanResultsSuccess() {
        showMessage("Scan Results Success")

        val results = mWifiManager.scanResults
        Log.d("Wifi", results.toString())
    }

    override fun scanResultsFailure() {
        showMessage("Scan Results Failure")
    }

    override fun scanRequestFailure() {
        showMessage("Scan Request Failure")
    }
}