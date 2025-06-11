package com.example.combuk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class OrdersListAdapter(private var orders: JSONArray) : RecyclerView.Adapter<OrdersListAdapter.OrderViewHolder>() {
    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderTime: TextView = itemView.findViewById(R.id.orderTime)
        val orderSummary: TextView = itemView.findViewById(R.id.orderSummary)
        val deleteButton: View = itemView.findViewById(R.id.deleteOrderButton)
    }

    var onDeleteOrder: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_summary, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order: JSONObject = orders.getJSONObject(position)
        val timestamp = order.optLong("timestamp", 0L)
        val items = order.getJSONArray("items")
        val timeStr = java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(java.util.Date(timestamp))
        holder.orderTime.text = timeStr
        val summary = StringBuilder()
        var totalPrice = 0.0
        for (i in 0 until items.length()) {
            val item = items.getJSONObject(i)
            summary.append("- ").append(item.getString("name")).append(" x").append(item.getInt("quantity"))
            val price = item.optDouble("price", 0.0)
            val quantity = item.optInt("quantity", 0)
            if (price > 0.0) {
                summary.append(" (" + String.format("%,.0f", price) + " $)")
            }
            summary.append("\n")
            totalPrice += price * quantity
        }
        summary.append("Total: ").append(String.format("%,.0f", totalPrice)).append(" $")
        holder.orderSummary.text = summary.toString().trim()
        holder.deleteButton.setOnClickListener {
            onDeleteOrder?.invoke(position)
        }
    }

    override fun getItemCount(): Int = orders.length()

    fun updateOrders(newOrders: JSONArray) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}
