package com.example.trackme

import android.net.wifi.ScanResult
import android.os.Build
import androidx.annotation.RequiresApi

class EnvironmentSimulation {
    class Wifi {
        @RequiresApi(Build.VERSION_CODES.R)
        fun getScanResults(): ArrayList<ScanResult> {
            val scanResults = ArrayList<ScanResult>()
            for (i in 0..10) {
                val sr = ScanResult()
                sr.SSID = "WiFi Name $i"
                sr.level = (-90..-30).random()
                sr.frequency = 2460
                scanResults.add(sr)
            }
            return scanResults
        }
    }

    class Bt {
        fun getBtScanResults(): ArrayList<String> {
            val scanResults = ArrayList<String>()
            for (i in 0..10) {
                scanResults.add("Bt Name $i")
            }
            return scanResults
        }
    }
}