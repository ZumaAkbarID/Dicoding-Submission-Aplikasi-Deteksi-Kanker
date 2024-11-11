package com.dicoding.asclepius.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.repository.HistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var rvHistory: RecyclerView
    private val list = ArrayList<History>()
    private lateinit var historyRepository: HistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvHistory = binding.rvHistory
        rvHistory.setHasFixedSize(true)

        historyRepository = HistoryRepository(application)

        getListHistory()
    }

    private fun getListHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val historyList = historyRepository.getAllHistory()

                withContext(Dispatchers.Main) {
                    if (historyList.isNotEmpty()) {
                        list.clear()
                        list.addAll(historyList)
                        showRecyclerList()
                    } else {
                        binding.tvEmptyHistory.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Gagal mengambil data")
                    binding.tvEmptyHistory.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showRecyclerList() {
        rvHistory.layoutManager = LinearLayoutManager(this)
        val adapter = ListHistoryAdapter(list)
        rvHistory.adapter = adapter
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}