package com.example.combuk

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class InventoryAdapter(private val items: JSONArray) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemQuantity: TextView = itemView.findViewById(R.id.itemQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item: JSONObject = items.getJSONObject(position)
        holder.itemName.text = item.getString("name")
        holder.itemQuantity.text = "Quantity: ${item.getInt("quantity")}" // Số lượng hiện tại
    }

    override fun getItemCount(): Int {
        val count = items.length()
        Log.d("InventoryAdapter", "Total items count: $count")
        return count
    }
}
