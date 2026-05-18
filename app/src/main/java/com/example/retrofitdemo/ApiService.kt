package com.example.retrofitdemo

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String = "general",
        @Query("lang") lang: String = "en",
        @Query("max") max: Int = 10,
        @Query("apikey") apiKey: String = Constants.API_KEY
    ): NewsResponse

    @GET("search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("lang") lang: String = "en",
        @Query("max") max: Int = 10,
        @Query("apikey") apiKey: String = Constants.API_KEY
    ): NewsResponse
}