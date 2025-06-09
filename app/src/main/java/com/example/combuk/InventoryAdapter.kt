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
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice) // Reference to item price view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item: JSONObject = items.getJSONObject(position)
        Log.d("PRICE_DEBUG", "[Inventory] Item: ${item.getString("name")}, price field: ${item.opt("price")}, price used: ${item.optInt("price", 0)}")
        holder.itemName.text = item.getString("name")
        holder.itemQuantity.text = "Stock: ${item.getInt("quantity")}" // Display stock quantity
        val price = item.optInt("price", 0)
        holder.itemPrice.text = if (price > 0) "$price₫" else "N/A"
    }

    override fun getItemCount(): Int {
        val count = items.length()
        Log.d("InventoryAdapter", "Total items count: $count")
        return count
    }
}
