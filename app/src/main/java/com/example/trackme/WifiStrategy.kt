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

abstract class WifiStrategy : AppCompatActivity() {
    protected open lateinit var mWifiManager: WifiManager

    private val kRequestWifiIdentifier = 345
    private val mRequiredPermissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.CHANGE_WIFI_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mWifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        requestWifiPermissionsAndStartScan()
    }

    private fun requestWifiPermissionsAndStartScan() {
        if (mRequiredPermissions
                .map { checkSelfPermission(it) }
                .all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            showMessage("Permissions are present")
            startWifiScan()
            return
        }

        if (mRequiredPermissions
                .map { shouldShowRequestPermissionRationale(it) }
                .any { it }
        ) {
            showMessage("Needs this permissions")
        }
        requestPermissions(mRequiredPermissions, kRequestWifiIdentifier)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != kRequestWifiIdentifier) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        // TODO this check is an obvious bug
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showMessage("Permission Granted")
            startWifiScan()
        }
    }

    private fun startWifiScan() {
        registerWifiBroadcastListener()
        val success = mWifiManager.startScan()
        if (!success) {
            scanRequestFailure()
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

    protected fun showMessage(text: String) {
        Snackbar.make(findViewById(R.id.activity_wifi), text, Snackbar.LENGTH_LONG).show()
    }

    protected abstract fun scanResultsSuccess()

    protected abstract fun scanRequestFailure()

    protected abstract fun scanResultsFailure()
}