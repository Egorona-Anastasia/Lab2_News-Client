package com.example.retrofitdemo

import com.google.gson.annotations.SerializedName

data class NewsArticle (val title: String, val description: String?, val content: String?, val url: String, val image: String?, @SerializedName("publishedAt") val publishedAt: String, val source: Source) {
    data class Source(val name: String) // источник инф
}
// заголовок, опись, содержание, ссылка на полную статью и изображение, дата публикации и объект Source.
// @SerializedName("publishedAt") - для случаев, где имя будет отличаться