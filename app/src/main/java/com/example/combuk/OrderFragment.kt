package com.example.combuk

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import android.widget.Button as WidgetButton

class OrderFragment : Fragment() {
    private lateinit var databaseManager: DatabaseManager
    private val selectedQuantities = mutableMapOf<Int, Int>()
    private lateinit var selectedItemCount: TextView
    private lateinit var totalPrice: TextView
    private lateinit var confirmOrderButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseManager = DatabaseManager(requireContext())
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewOrder)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val items = databaseManager.getMenuItems()
        selectedItemCount = view.findViewById(R.id.selectedItemCount)
        totalPrice = view.findViewById(R.id.totalPrice)
        confirmOrderButton = view.findViewById(R.id.confirmOrderButton)
        val statusBar = selectedItemCount.parent as View // LinearLayout chứa status bar

        val adapter = OrderAdapter(items) { position, newSelectedQuantity ->
            selectedQuantities[position] = newSelectedQuantity
            updateStatusBar(items)
        }
        recyclerView.adapter = adapter

        confirmOrderButton.setOnClickListener {
            val selectedItems = JSONArray()
            for ((position, quantity) in selectedQuantities) {
                if (quantity > 0) {
                    val item = items.getJSONObject(position)
                    val orderItem = JSONObject()
                    orderItem.put("name", item.getString("name"))
                    orderItem.put("quantity", quantity)
                    selectedItems.put(orderItem)
                }
            }
            if (selectedItems.length() > 0) {
                val order = JSONObject()
                order.put("timestamp", System.currentTimeMillis())
                order.put("items", selectedItems)
                databaseManager.addOrder(order)
                Toast.makeText(requireContext(), "Order confirmed!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "No items selected!", Toast.LENGTH_SHORT).show()
            }
        }

        // Show selected items detail dialog when clicking the status bar
        statusBar.setOnClickListener {
            val details = StringBuilder()
            for ((position, quantity) in selectedQuantities) {
                if (quantity > 0) {
                    val item = items.getJSONObject(position)
                    val name = item.getString("name")
                    val price = item.optInt("price", 0)
                    details.append("$name x$quantity ($${price * quantity})\n")
                }
            }
            val message = if (details.isNotEmpty()) details.toString() else "No items selected."
            AlertDialog.Builder(requireContext())
                .setTitle("Selected Items")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    // Update the status bar with the total selected items and total price
    private fun updateStatusBar(items: JSONArray) {
        var totalSelectedItems = 0
        var totalPriceValue = 0
        for ((position, selected) in selectedQuantities) {
            val item = items.getJSONObject(position)
            val price = item.optInt("price", 0)
            totalSelectedItems += selected
            totalPriceValue += selected * price
        }
        selectedItemCount.text = "Items: $totalSelectedItems"
        totalPrice.text = "Price: $$totalPriceValue"
    }
}
