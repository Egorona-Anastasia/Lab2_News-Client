package com.example.retrofitdemo

data class NewsResponse(
    val articles: List<NewsArticle>,
    val totalResults: Int
)