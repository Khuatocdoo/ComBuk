package com.example.combuk

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class OrderAdapter(private val items: JSONArray, private val onQuantityChange: (Int, Int) -> Unit) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemQuantity: TextView = itemView.findViewById(R.id.itemQuantity)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice) // Reference to the price TextView
        val incrementButton: Button = itemView.findViewById(R.id.incrementButton)
        val decrementButton: Button = itemView.findViewById(R.id.decrementButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item: JSONObject = items.getJSONObject(position)
        Log.d("PRICE_DEBUG", "[Order] Item: ${item.getString("name")}, price field: ${item.opt("price")}, price used: ${item.optInt("price", 0)}")
        holder.itemName.text = item.getString("name")
        val initialQuantity = item.getInt("quantity")
        holder.itemQuantity.text = "0" // Default selected quantity
        val price = item.optInt("price", 0)
        holder.itemPrice.text = if (price > 0) "$${price}" else "N/A" // Show price with $

        holder.incrementButton.setOnClickListener {
            val currentSelectedQuantity = holder.itemQuantity.text.toString().toInt()
            if (initialQuantity > currentSelectedQuantity) {
                val newSelectedQuantity = currentSelectedQuantity + 1
                holder.itemQuantity.text = "$newSelectedQuantity"
                onQuantityChange(position, newSelectedQuantity)
                Log.d("OrderAdapter", "Incremented quantity for ${item.getString("name")}: $newSelectedQuantity")
            }
        }

        holder.decrementButton.setOnClickListener {
            val currentSelectedQuantity = holder.itemQuantity.text.toString().toInt()
            if (currentSelectedQuantity > 0) {
                val newSelectedQuantity = currentSelectedQuantity - 1
                holder.itemQuantity.text = "$newSelectedQuantity"
                onQuantityChange(position, newSelectedQuantity)
                Log.d("OrderAdapter", "Decremented quantity for ${item.getString("name")}: $newSelectedQuantity")
            }
        }
    }

    override fun getItemCount(): Int {
        return items.length()
    }
}
