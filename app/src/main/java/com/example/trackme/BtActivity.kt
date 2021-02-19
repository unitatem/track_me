package com.example.trackme

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

data class BtPointMetadata(val name: String?, val macAddress: String)

class BtAdapter(private val dataSet: ArrayList<BtPointMetadata>) :
    RecyclerView.Adapter<BtAdapter.BtItemViewHolder>() {

    class BtItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btName: TextView = view.findViewById(R.id.bt_name)
        val btMACAddress: TextView = view.findViewById(R.id.bt_mac_address)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BtItemViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.bt_item_view, viewGroup, false)
        return BtItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: BtItemViewHolder, position: Int) {
        viewHolder.btName.text = dataSet[position].name
        viewHolder.btMACAddress.text = dataSet[position].macAddress
    }

    override fun getItemCount() = dataSet.size
}

class BtActivity : AppCompatActivity() {
    private val TAG: String = BtActivity::class.qualifiedName.toString()

    private class RequestCode {
        companion object {
            const val kRequestEnableBt: Int = 45652
        }
    }

    private val btResult: ArrayList<BtPointMetadata> = arrayListOf()
    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate >")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bt)

        val recyclerViewBt = findViewById<View>(R.id.rv_bt) as RecyclerView
        recyclerViewBt.adapter = BtAdapter(btResult)
        recyclerViewBt.layoutManager = LinearLayoutManager(this)

        if (bluetoothAdapter == null) {
            Log.i(TAG, "Device doesn't support Bluetooth")
            showMessage("Device doesn't support Bluetooth")
            return
        }

        registerBtListener()
        enableAndScanBt()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy >")
        super.onDestroy()

        unregisterReceiver(mBtBroadcastReceiver)
    }

    private fun registerBtListener() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mBtBroadcastReceiver, filter)
    }

    private fun enableAndScanBt() {
        if (bluetoothAdapter?.isEnabled == false) {
            Log.v(TAG, "Request to enable BT")
            showMessage("Request to enable BT")

            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, RequestCode.kRequestEnableBt)
        } else {
            scanBtDevices()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult > {requestCode=$requestCode, resultCode=$resultCode}")

        if (requestCode != RequestCode.kRequestEnableBt) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        if (resultCode != RESULT_OK) {
            Log.e(TAG, "BT failed to enable. Error=$resultCode")
            showMessage("BT failed to enable. $resultCode")
            return
        }

        scanBtDevices()
    }

    private fun scanBtDevices() {
        Log.d(TAG, "scanBtDevices >")
        showMessage("BT enabled")

        bluetoothAdapter?.startDiscovery()

        // clean recycler view
        btResult.clear()
        val recyclerViewBt = findViewById<View>(R.id.rv_bt) as RecyclerView
        recyclerViewBt.adapter?.notifyDataSetChanged()
    }

    private val mBtBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return

                    val deviceName = device.name
                    val deviceMACAddress = device.address
                    Log.v(TAG, "BT device found: {deviceName=$deviceName, deviceMacAddress=$deviceMACAddress}")

                    val record = BtPointMetadata(deviceName, deviceMACAddress)
                    btResult.add(record)
                    val recyclerViewBt = findViewById<View>(R.id.rv_bt) as RecyclerView
                    recyclerViewBt.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun showMessage(text: String) {
        Snackbar.make(findViewById(R.id.activity_bt), text, Snackbar.LENGTH_LONG).show()
    }
}