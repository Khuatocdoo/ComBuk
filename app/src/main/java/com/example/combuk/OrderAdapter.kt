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

    // Thêm thuộc tính để nhận maxSelectedItems và selectedQuantities từ Fragment
    var maxSelectedItems: Int = 1
    var selectedQuantities: Map<Int, Int> = emptyMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item: JSONObject = items.getJSONObject(position)
        holder.itemName.text = item.getString("name")
        val initialQuantity = item.getInt("quantity")
        val selected = selectedQuantities[position] ?: 0
        holder.itemQuantity.text = selected.toString()
        val price = item.optInt("price", 0)
        holder.itemPrice.text = if (price > 0) "$${price}" else "N/A"

        // Highlight if selected
        if (selected > 0) {
            holder.itemView.setBackgroundColor(0xFFE0F7FA.toInt()) // Light blue
        } else {
            holder.itemView.setBackgroundColor(0x00000000) // Transparent
        }

        holder.incrementButton.setOnClickListener {
            val currentSelectedQuantity = selectedQuantities[position] ?: 0
            val totalSelected = selectedQuantities.values.sum() - currentSelectedQuantity
            if (totalSelected + 1 > maxSelectedItems) {
                // Không tăng nếu vượt quá giới hạn suất ăn
                return@setOnClickListener
            }
            val newSelectedQuantity = currentSelectedQuantity + 1
            onQuantityChange(position, newSelectedQuantity)
        }

        holder.decrementButton.setOnClickListener {
            val currentSelectedQuantity = holder.itemQuantity.text.toString().toInt()
            if (currentSelectedQuantity > 0) {
                val newSelectedQuantity = currentSelectedQuantity - 1
                holder.itemQuantity.text = "$newSelectedQuantity"
                onQuantityChange(position, newSelectedQuantity)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.length()
    }
}
