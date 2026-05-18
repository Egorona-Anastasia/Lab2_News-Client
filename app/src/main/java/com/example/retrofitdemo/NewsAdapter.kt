package com.example.retrofitdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.retrofitdemo.databinding.ItemNewsBinding
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(private val onItemClick: (String) -> Unit) : ListAdapter<NewsArticle, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, viewPosition: Int) {
        holder.bind(getItem(viewPosition))
    }

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: NewsArticle) {
            binding.apply {
                tvTitle.text = article.title
                tvDescription.text = article.description ?: "Нет описания"

                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                val date = inputFormat.parse(article.publishedAt)
                tvDate.text = date?.let { outputFormat.format(it) } ?: article.publishedAt

                ivNewsImage.load(article.image) {
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }

                // отслеживание клика
                root.setOnClickListener {
                    onItemClick(article.url)   // передаём URL выбраной новости
                }
            }
        }
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<NewsArticle>() {
        override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean =
            oldItem == newItem
    }
}