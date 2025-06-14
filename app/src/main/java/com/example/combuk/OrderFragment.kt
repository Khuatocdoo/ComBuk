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
import android.widget.RadioButton
import android.widget.RadioGroup

class OrderFragment : Fragment() {
    private lateinit var databaseManager: DatabaseManager
    private val selectedQuantities = mutableMapOf<Int, Int>()
    private lateinit var selectedItemCount: TextView
    private lateinit var totalPrice: TextView
    private lateinit var confirmOrderButton: Button
    private var maxSelectedItems = 1

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
        val radioGroup = view.findViewById<RadioGroup>(R.id.priceRadioGroup)
        val radio35 = view.findViewById<RadioButton>(R.id.radio35)
        val radio45 = view.findViewById<RadioButton>(R.id.radio45)
        val radio55 = view.findViewById<RadioButton>(R.id.radio55)

        // Default: $35
        radio35.isChecked = true
        maxSelectedItems = 1
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            maxSelectedItems = when (checkedId) {
                R.id.radio35 -> 1
                R.id.radio45 -> 2
                R.id.radio55 -> 3
                else -> 1
            }
            // Nếu tổng số lượng đã chọn vượt quá giới hạn mới, reset hết lựa chọn
            if (selectedQuantities.values.sum() > maxSelectedItems) {
                selectedQuantities.keys.forEach { selectedQuantities[it] = 0 }
                updateStatusBar(items)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        val adapter = OrderAdapter(items, onQuantityChange = { position, newSelectedQuantity ->
            // Cập nhật selectedQuantities và maxSelectedItems cho adapter mỗi lần thay đổi
            recyclerView.post {
                (recyclerView.adapter as? OrderAdapter)?.selectedQuantities = selectedQuantities
                (recyclerView.adapter as? OrderAdapter)?.maxSelectedItems = maxSelectedItems
            }

            val currentTotal = selectedQuantities.values.sum() - (selectedQuantities[position] ?: 0)
            if (currentTotal + newSelectedQuantity > maxSelectedItems) {
                Toast.makeText(requireContext(), "Bạn chỉ được chọn tối đa $maxSelectedItems món!", Toast.LENGTH_SHORT).show()
                recyclerView.adapter?.notifyItemChanged(position)
                return@OrderAdapter
            }
            selectedQuantities[position] = newSelectedQuantity
            (recyclerView.adapter as? OrderAdapter)?.selectedQuantities = selectedQuantities
            updateStatusBar(items)
            recyclerView.adapter?.notifyItemChanged(position)
        })
        adapter.selectedQuantities = selectedQuantities
        adapter.maxSelectedItems = maxSelectedItems
        recyclerView.adapter = adapter

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            maxSelectedItems = when (checkedId) {
                R.id.radio35 -> 1
                R.id.radio45 -> 2
                R.id.radio55 -> 3
                else -> 1
            }
            adapter.maxSelectedItems = maxSelectedItems
            // Nếu tổng số lượng đã chọn vượt quá giới hạn mới, reset hết lựa chọn
            if (selectedQuantities.values.sum() > maxSelectedItems) {
                selectedQuantities.keys.forEach { selectedQuantities[it] = 0 }
                adapter.selectedQuantities = selectedQuantities
                updateStatusBar(items)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
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
                // Reset selections and status bar
                selectedQuantities.keys.forEach { selectedQuantities[it] = 0 }
                (recyclerView.adapter as? OrderAdapter)?.selectedQuantities = selectedQuantities
                updateStatusBar(items)
                recyclerView.adapter?.notifyDataSetChanged()
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
        for ((_, selected) in selectedQuantities) {
            totalSelectedItems += selected
        }
        selectedItemCount.text = "Items: $totalSelectedItems"
        // Lấy giá trị từ RadioButton
        val radioGroup = view?.findViewById<RadioGroup>(R.id.priceRadioGroup)
        val price = when (radioGroup?.checkedRadioButtonId) {
            R.id.radio35 -> 35
            R.id.radio45 -> 45
            R.id.radio55 -> 55
            else -> 0
        }
        totalPrice.text = "Price: $$price"
    }
}
