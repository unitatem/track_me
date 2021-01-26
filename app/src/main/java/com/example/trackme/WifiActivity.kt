package com.example.trackme

import android.net.wifi.ScanResult
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class WiFiAdapter(private val dataSet: ArrayList<ScanResult>) :
    RecyclerView.Adapter<WiFiAdapter.WifiViewHolder>() {

    class WifiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wifiName: TextView = view.findViewById(R.id.wifi_name)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): WifiViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.wifi_item_view, viewGroup, false)
        return WifiViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: WifiViewHolder, position: Int) {
        viewHolder.wifiName.text = dataSet[position].SSID
    }

    override fun getItemCount() = dataSet.size
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

    override fun scanResultsSuccess() {
        showMessage("Scan Results Success")

        wifiScanResults.clear()
        wifiScanResults.addAll(mWifiManager.scanResults)
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