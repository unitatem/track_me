package com.example.trackme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BtAdapter(private val dataSet: ArrayList<String>) :
    RecyclerView.Adapter<BtAdapter.BtItemViewHolder>() {

    class BtItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btName: TextView = view.findViewById(R.id.bt_name)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BtItemViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.bt_item_view, viewGroup, false)
        return BtItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: BtItemViewHolder, position: Int) {
        viewHolder.btName.text = dataSet[position]
    }

    override fun getItemCount() = dataSet.size
}

class BtActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bt)

        val recyclerViewBt = findViewById<View>(R.id.rv_bt) as RecyclerView
        recyclerViewBt.adapter = BtAdapter(EnvironmentSimulation.Bt().getBtScanResults())
        recyclerViewBt.layoutManager = LinearLayoutManager(this)
    }
}