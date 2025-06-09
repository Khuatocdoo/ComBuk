package com.example.combuk

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class InventoryActivity : AppCompatActivity() {

    private lateinit var databaseManager: DatabaseManager
    private lateinit var inventoryRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        Log.d("InventoryActivity", "onCreate called")

        databaseManager = DatabaseManager(this)

        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerView)
        inventoryRecyclerView.layoutManager = LinearLayoutManager(this)

        displayInventory()
    }

    private fun displayInventory() {
        Log.d("InventoryActivity", "displayInventory called")
        val items = databaseManager.getMenuItems()
        Log.d("InventoryActivity", "Items loaded: $items")

        val adapter = InventoryAdapter(items)
        inventoryRecyclerView.adapter = adapter
        Log.d("InventoryActivity", "Adapter set with items")
    }
}
