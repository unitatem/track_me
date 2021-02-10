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


class WiFiAdapter(private val dataSet: ArrayList<ScanResult>) :
    RecyclerView.Adapter<WiFiAdapter.WifiItemViewHolder>() {

    class WifiItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wifiName: TextView = view.findViewById(R.id.wifi_name)
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
        viewHolder.wifiName.text = dataSet[position].SSID

        viewHolder.wifiRssi.text = viewHolder.itemView.context.getString(
            R.string.wifi_rssi, dataSet[position].level
        )

        viewHolder.wifiFrequency.text = viewHolder.itemView.context.getString(
            R.string.wifi_frequency,
            dataSet[position].frequency
        )

        viewHolder.wifiDistance.text = viewHolder.itemView.context.getString(
            R.string.wifi_distance, WifiDistance().getDistance(
                dataSet[position].level.toDouble(),
                WifiDistance.convertMHz2Hz(dataSet[position].frequency.toDouble())
            )
        )
    }

    override fun getItemCount() = dataSet.size

    companion object {
        fun sort(data: ArrayList<ScanResult>) {
            data.sortWith(Comparator { lhs, rhs -> rhs.level - lhs.level })
        }
    }
}

class WifiActivity : WifiStrategy() {
    private var wifiScanResults: ArrayList<ScanResult> = arrayListOf<ScanResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_wifi)
        super.onCreate(savedInstanceState)

        val recyclerViewWifi = findViewById<View>(R.id.rv_wifi) as RecyclerView
        recyclerViewWifi.adapter = WiFiAdapter(wifiScanResults)
        recyclerViewWifi.layoutManager = LinearLayoutManager(this)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun scanResultsSuccess() {
        showMessage("Scan Results Success")

        val scanResults = if (EnvironmentInfo().isRealDevice()) {
            mWifiManager.scanResults
        } else {
            EnvironmentSimulation.Wifi().getScanResults()
        }
        WiFiAdapter.sort(scanResults as ArrayList<ScanResult>)

        wifiScanResults.clear()
        wifiScanResults.addAll(scanResults)
        Log.d("Wifi", wifiScanResults.toString())

        val recyclerViewWifi = findViewById<View>(R.id.rv_wifi) as RecyclerView
        recyclerViewWifi.adapter?.notifyDataSetChanged()
    }

    override fun scanResultsFailure() {
        showMessage("Scan Results Failure")
    }

    override fun scanRequestFailure() {
        showMessage("Scan Request Failure")
    }
}