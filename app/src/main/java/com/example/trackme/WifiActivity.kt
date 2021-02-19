package com.example.trackme

import android.net.wifi.ScanResult
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class WifiPointMetadata(val scanResult: ScanResult, val distance: Double)

class WiFiAdapter(private val dataSet: ArrayList<WifiPointMetadata>) :
    RecyclerView.Adapter<WiFiAdapter.WifiItemViewHolder>() {

    class WifiItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wifiName: TextView = view.findViewById(R.id.wifi_name)
        val wifiMacAddress: TextView = view.findViewById(R.id.wifi_mac_address)
        val wifiRssi: TextView = view.findViewById(R.id.wifi_rssi)
        val wifiFrequency: TextView = view.findViewById(R.id.wifi_frequency)
        val wifiDistance: TextView = view.findViewById(R.id.wifi_distance)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): WifiItemViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.wifi_item_view, viewGroup, false)
        return WifiItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: WifiItemViewHolder, position: Int) {
        viewHolder.wifiName.text = dataSet[position].scanResult.SSID
        viewHolder.wifiMacAddress.text = dataSet[position].scanResult.BSSID

        viewHolder.wifiRssi.text = viewHolder.itemView.context.getString(
            R.string.wifi_rssi, dataSet[position].scanResult.level)

        viewHolder.wifiFrequency.text = viewHolder.itemView.context.getString(
            R.string.wifi_frequency, dataSet[position].scanResult.frequency)

        viewHolder.wifiDistance.text = viewHolder.itemView.context.getString(
            R.string.wifi_distance, dataSet[position].distance)
    }

    override fun getItemCount() = dataSet.size

    companion object {
        fun sort(data: ArrayList<WifiPointMetadata>) {
            data.sortWith(Comparator { lhs, rhs ->
                when {
                    lhs.distance < rhs.distance -> -1
                    lhs.distance > rhs.distance -> 1
                    else -> 0
                }
            })
        }
    }
}

class WifiActivity : WifiStrategy() {
    private val TAG: String = "WifiActivity"
    private var wifiScanResults: ArrayList<WifiPointMetadata> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate >")
        setContentView(R.layout.activity_wifi)
        super.onCreate(savedInstanceState)

        val recyclerViewWifi = findViewById<View>(R.id.rv_wifi) as RecyclerView
        recyclerViewWifi.adapter = WiFiAdapter(wifiScanResults)
        recyclerViewWifi.layoutManager = LinearLayoutManager(this)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun scanResultsSuccess() {
        Log.d(TAG, "scanResultsSuccess >")
        showMessage("Scan Results Success")

        val rawScanResults = if (EnvironmentInfo().isRealDevice()) {
            mWifiManager.scanResults
        } else {
            EnvironmentSimulation.Wifi().getScanResults()
        }
        val scanResults = rawScanResults.map { it ->
            WifiPointMetadata(
                it,
                WifiDistance().getDistance(
                    it.level.toDouble(),
                    WifiDistance.convertMHz2Hz(it.frequency.toDouble())))
        }

        WiFiAdapter.sort(scanResults as ArrayList<WifiPointMetadata>)

        wifiScanResults.clear()
        wifiScanResults.addAll(scanResults)
        Log.v(TAG, "{wifiScanResults=$wifiScanResults}")

        val recyclerViewWifi = findViewById<View>(R.id.rv_wifi) as RecyclerView
        recyclerViewWifi.adapter?.notifyDataSetChanged()
    }

    override fun scanResultsFailure() {
        Log.d(TAG, "scanResultsFailure >")
        showMessage("Scan Results Failure")
    }

    override fun scanRequestFailure() {
        Log.d(TAG, "scanRequestFailure >")
        showMessage("Scan Request Failure")
    }
}