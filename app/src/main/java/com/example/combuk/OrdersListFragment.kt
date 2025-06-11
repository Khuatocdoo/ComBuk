package com.example.combuk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class OrdersListFragment : Fragment() {
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var ordersAdapter: OrdersListAdapter
    private lateinit var databaseManager: DatabaseManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_orders_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseManager = DatabaseManager(requireContext())
        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val orders = databaseManager.getOrders() // JSONArray
        android.util.Log.d("OrdersListFragment", "orders.length = "+orders.length())
        for (i in 0 until orders.length()) {
            android.util.Log.d("OrdersListFragment", "order[$i] = "+orders.getJSONObject(i))
        }
        ordersAdapter = OrdersListAdapter(orders)
        ordersRecyclerView.adapter = ordersAdapter
        ordersAdapter.onDeleteOrder = { position ->
            val currentOrders = databaseManager.getOrders()
            val newOrders = JSONArray()
            for (i in 0 until currentOrders.length()) {
                if (i != position) newOrders.put(currentOrders.getJSONObject(i))
            }
            // Lưu lại file orders.json mới
            databaseManager.saveOrders(newOrders)
            // Cập nhật lại adapter
            ordersAdapter.updateOrders(newOrders)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload orders and update adapter
        val orders = databaseManager.getOrders()
        ordersAdapter.updateOrders(orders)
    }
}
