package com.example.trackme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar

abstract class WifiStrategy : AppCompatActivity() {
    private val TAG: String = WifiStrategy::class.qualifiedName.toString()

    private class RequestCode {
        companion object {
            const val kRequestWifiPermissions = 345
        }
    }

    protected open lateinit var mWifiManager: WifiManager

    private val mRequiredPermissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.CHANGE_WIFI_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate >")
        super.onCreate(savedInstanceState)

        mWifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        requestWifiPermissionsAndStartScan()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy >")
        super.onDestroy()

        unregisterReceiver(mWifiScanBroadcastReceiver)
    }

    private fun requestWifiPermissionsAndStartScan() {
        Log.d(TAG, "requestWifiPermissionsAndStartScan >")
        if (mRequiredPermissions
                .map { checkSelfPermission(it) }
                .all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            Log.d(TAG, "Permissions are present")
            startWifiScan()
            return
        }

        if (mRequiredPermissions
                .map { shouldShowRequestPermissionRationale(it) }
                .any { it }
        ) {
            showMessage("Needs this permissions")
        }
        requestPermissions(mRequiredPermissions, RequestCode.kRequestWifiPermissions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult > {requestCode=$requestCode}")
        if (requestCode != RequestCode.kRequestWifiPermissions) {
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
        Log.d(TAG, "startWifiScan >")
        registerWifiBroadcastListener()
        val success = mWifiManager.startScan()
        if (!success) {
            scanRequestFailure()
        }
    }

    private fun registerWifiBroadcastListener() {
        Log.d(TAG, "registerWifiBroadcastListener >")

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(mWifiScanBroadcastReceiver, intentFilter)
    }

    private val mWifiScanBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanResultsSuccess()
            } else {
                scanResultsFailure()
            }
        }
    }

    protected fun showMessage(text: String) {
        Snackbar.make(findViewById(R.id.activity_wifi), text, Snackbar.LENGTH_LONG).show()
    }

    protected abstract fun scanResultsSuccess()

    protected abstract fun scanRequestFailure()

    protected abstract fun scanResultsFailure()
}