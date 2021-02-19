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

class BtPointMetadata(val name: String?, val macAddress: String)

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
    private val TAG: String = "BtActivity"
    private val REQUEST_ENABLE_BT: Int = 45652

    private val btResult: ArrayList<BtPointMetadata> = arrayListOf()

    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate >")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bt)

        val recyclerViewBt = findViewById<View>(R.id.rv_bt) as RecyclerView
        recyclerViewBt.adapter = BtAdapter(btResult)
        recyclerViewBt.layoutManager = LinearLayoutManager(this)

        // getBTAdapter
        if (bluetoothAdapter == null) {
            showMessage("Device doesn't support Bluetooth")
            return
        }

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        // enableBT
        if (bluetoothAdapter?.isEnabled == false) {
            showMessage("Request to enable BT")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            btEnabled()
        }
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy >")
        super.onDestroy()

        unregisterReceiver(receiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "onActivityResult > {requestCode=$requestCode, resultCode=$resultCode}")
        if (requestCode != REQUEST_ENABLE_BT) {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (resultCode == RESULT_OK) {
            showMessage("BT enabled")
            btEnabled()
        } else {
            showMessage("BT failed to enable. $resultCode")
        }
    }

    private fun btEnabled() {
        Log.d(TAG, "btEnabled >")
        bluetoothAdapter?.startDiscovery()

        // clean recycler view
        btResult.clear()
        val recyclerViewBt = findViewById<View>(R.id.rv_bt) as RecyclerView
        recyclerViewBt.adapter?.notifyDataSetChanged()
    }

    private val receiver = object : BroadcastReceiver() {
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