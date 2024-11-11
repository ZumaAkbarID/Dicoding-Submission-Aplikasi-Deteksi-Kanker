package com.dicoding.asclepius.data.retrofit

import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.response.NewsResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("top-headlines")
    fun getNews(
        @Query("q") q: String = "Cancer",
        @Query("category") category: String = "health",
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String = BuildConfig.ApiKey,
    ): Call<NewsResponse>
}