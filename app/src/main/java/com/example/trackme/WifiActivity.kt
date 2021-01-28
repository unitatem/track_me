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
    RecyclerView.Adapter<WiFiAdapter.WifiViewHolder>() {

    class WifiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wifiName: TextView = view.findViewById(R.id.wifi_name)
        val wifiStrength: TextView = view.findViewById(R.id.wifi_strength)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): WifiViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.wifi_item_view, viewGroup, false)
        return WifiViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: WifiViewHolder, position: Int) {
        viewHolder.wifiName.text = dataSet[position].SSID
        viewHolder.wifiStrength.text = dataSet[position].level.toString()
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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun scanResultsSuccess() {
        showMessage("Scan Results Success")

        wifiScanResults.clear()
        wifiScanResults.addAll(mWifiManager.scanResults)
        Log.d("Wifi", wifiScanResults.toString())

        val recyclerViewWifi = findViewById<View>(R.id.rv_wifi) as RecyclerView
        recyclerViewWifi.adapter?.notifyDataSetChanged()

        // provide mocked data in emulator
        if (EnvironmentInfo().isRealDevice()) return

        val scanResults = ArrayList<ScanResult>()
        for (i in 0..10) {
            val sr = ScanResult()
            sr.SSID = "WiFi Name $i"
            sr.level = (-100..-10).random()
            scanResults.add(sr)
        }

        wifiScanResults.clear()
        wifiScanResults.addAll(scanResults)

        recyclerViewWifi.adapter?.notifyDataSetChanged()
    }

    override fun scanResultsFailure() {
        showMessage("Scan Results Failure")
    }

    override fun scanRequestFailure() {
        showMessage("Scan Request Failure")
    }
}