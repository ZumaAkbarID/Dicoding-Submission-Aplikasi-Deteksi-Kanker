package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.data.response.NewsResponse
import com.dicoding.asclepius.data.retrofit.ApiConfig
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvNews.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvNews.addItemDecoration(itemDecoration)

        loadNews()
    }

    private fun loadNews() {
        showLoading(true)
        val client = ApiConfig.getApiService().getNews()
        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(
                call: Call<NewsResponse>,
                response: Response<NewsResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val adapter = NewsAdapter()
                        adapter.submitList(responseBody.articles)
                        binding.rvNews.adapter = adapter
                    }
                } else {
                    showToast("Gagal mengambil respon dari server")
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                showLoading(false)
                showToast("Terjadi kesalahan saat load berita")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}