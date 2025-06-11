package com.example.combuk

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class OrderActivity : AppCompatActivity() {

    private lateinit var databaseManager: DatabaseManager

    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var selectedItemCount: TextView
    private lateinit var totalPrice: TextView
    private lateinit var confirmOrderButton: Button

    private val selectedQuantities = mutableMapOf<Int, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        databaseManager = DatabaseManager(this)

        orderRecyclerView = findViewById(R.id.orderRecyclerView)
        orderRecyclerView.layoutManager = LinearLayoutManager(this)

        selectedItemCount = findViewById(R.id.selectedItemCount)
        totalPrice = findViewById(R.id.totalPrice)
        confirmOrderButton = findViewById(R.id.confirmOrderButton)

        displayItems()

        confirmOrderButton.setOnClickListener {
            val selectedItems = JSONArray()
            val items = databaseManager.getMenuItems()

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
                Toast.makeText(this, "Order confirmed!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No items selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayItems() {
        val items = databaseManager.getMenuItems()
        val adapter = OrderAdapter(items, onQuantityChange = { position, newSelectedQuantity ->
            selectedQuantities[position] = newSelectedQuantity
            updateStatusBar(items)
        })
        orderRecyclerView.adapter = adapter
    }

    private fun updateStatusBar(items: JSONArray) {
        var totalSelectedItems = 0
        var totalPrice = 0
        for ((position, selected) in selectedQuantities) {
            val item = items.getJSONObject(position)
            val price = item.optInt("price", 0)
            Log.d("PRICE_DEBUG", "[StatusBar] Item: ${item.getString("name")}, selected: $selected, price: $price, subtotal: ${selected * price}")
            totalSelectedItems += selected
            totalPrice += selected * price
        }
        selectedItemCount.text = "Items: $totalSelectedItems"
        this.totalPrice.text = "Price: $$totalPrice"
    }
}