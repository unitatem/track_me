package com.example.trackme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar

class WifiActivity : AppCompatActivity() {
    private lateinit var mWifiManager: WifiManager
    private val kRequestWifi = 345
    private val mRequiredPermissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.CHANGE_WIFI_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)

        mWifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        requestWifiPermissions()
    }

    private fun requestWifiPermissions() {
        if (mRequiredPermissions
                .map { checkSelfPermission(it) }
                .all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            Snackbar.make(
                findViewById(R.id.activity_wifi),
                "WifiActivity: Permissions are present",
                Snackbar.LENGTH_LONG
            ).show()
            startWifiScan()
            return
        }
        if (mRequiredPermissions
                .map { shouldShowRequestPermissionRationale(it) }
                .any { it }
        ) {
            Snackbar.make(
                findViewById(R.id.activity_wifi),
                "WifiActivity: Needs this permissions",
                Snackbar.LENGTH_LONG
            ).show()
        }
        requestPermissions(mRequiredPermissions, kRequestWifi)
    }

    private fun startWifiScan() {
        registerWifiBroadcastListener()
        val success = mWifiManager.startScan()
        if (!success) {
            scanRequestFailure()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != kRequestWifi) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(
                findViewById(R.id.activity_wifi),
                "Permission Granted",
                Snackbar.LENGTH_LONG
            ).show()
            startWifiScan()
        }
    }

    private fun registerWifiBroadcastListener() {
        val wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success) {
                    scanResultsSuccess()
                } else {
                    scanResultsFailure()
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)
    }

    private fun scanResultsSuccess() {
        Snackbar.make(
            findViewById(R.id.activity_wifi),
            "Scan Results Success",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun scanRequestFailure() {
        Snackbar.make(
            findViewById(R.id.activity_wifi),
            "Scan Request Failure",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun scanResultsFailure() {
        Snackbar.make(
            findViewById(R.id.activity_wifi),
            "Scan Results Failure",
            Snackbar.LENGTH_LONG
        ).show()
    }
}