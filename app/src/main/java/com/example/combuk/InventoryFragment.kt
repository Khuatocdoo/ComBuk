package com.example.combuk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InventoryFragment : Fragment() {
    private lateinit var databaseManager: DatabaseManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseManager = DatabaseManager(requireContext())
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewInventory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val items = databaseManager.getMenuItems()
        val adapter = InventoryAdapter(items)
        recyclerView.adapter = adapter
    }
}
