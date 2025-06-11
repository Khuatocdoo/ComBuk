package com.example.combuk

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class DatabaseManager(private val context: Context) {

    private val menuFileName = "menu.json"
    private val ordersFileName = "orders.json"

    init {
        copyAssetToFileIfNotExists(menuFileName)
        copyAssetToFileIfNotExists(ordersFileName)
    }

    private fun copyAssetToFileIfNotExists(fileName: String) {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            val inputStream = context.assets.open(fileName)
            val content = inputStream.bufferedReader().use { it.readText() }
            file.writeText(content)
        }
    }

    fun getMenuItems(): JSONArray {
        val file = File(context.filesDir, menuFileName)
        val json = JSONObject(file.readText())
        return json.getJSONArray("items")
    }

    fun getOrders(): JSONArray {
        val file = File(context.filesDir, ordersFileName)
        val json = JSONObject(file.readText())
        return json.getJSONArray("orders")
    }

    fun addOrder(order: JSONObject) {
        val file = File(context.filesDir, ordersFileName)
        val json = if (file.exists()) JSONObject(file.readText()) else JSONObject().apply { put("orders", JSONArray()) }
        val orders = json.getJSONArray("orders")
        orders.put(order)
        file.writeText(json.toString())
        Log.d("ORDER_DEBUG", "Order added: $order")
    }

    fun updateMenuItems(items: JSONArray) {
        val file = File(context.filesDir, menuFileName)
        val json = JSONObject()
        json.put("items", items)
        file.writeText(json.toString())
    }

    fun saveOrders(newOrders: JSONArray) {
        val file = File(context.filesDir, ordersFileName)
        val json = JSONObject().apply { put("orders", newOrders) }
        file.writeText(json.toString())
    }
}
