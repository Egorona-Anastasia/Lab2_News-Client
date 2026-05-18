package com.example.retrofitdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView

    private lateinit var categorySpinner: Spinner
    private lateinit var newsAdapter: NewsAdapter

    //для поиска по категориям/без
    private var currentCategory = "general"
    private var isSearchMode = false

    private var isFetching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newsRecyclerView = findViewById(R.id.newsRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        searchView = findViewById(R.id.searchView)
        categorySpinner = findViewById(R.id.categorySpinner)

        // Создаём адаптер с обработчиком кликов
        newsAdapter = NewsAdapter { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        newsRecyclerView.adapter = newsAdapter
        newsRecyclerView.layoutManager = LinearLayoutManager(this)

        loadTopHeadlines(currentCategory)

        //категория
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!isSearchMode) { // не даёт переключать категорию во время поиска
                    val category = parent?.getItemAtPosition(position).toString().lowercase()
                    currentCategory = category
                    loadTopHeadlines(currentCategory)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        swipeRefreshLayout.setOnRefreshListener {
            if (isSearchMode) {
                // если до этого был поиск - сбрасываем запрос и возвращаемся к категориям
                searchView.setQuery("", false)
                searchView.clearFocus()
                isSearchMode = false
                categorySpinner.isEnabled = true
                loadTopHeadlines(currentCategory)
            } else {
                loadTopHeadlines(currentCategory)
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    isSearchMode = true
                    categorySpinner.isEnabled = false
                    searchNews(query)
                } else {
                    // если запрос пустой – возвращаемся к обычному режиму
                    isSearchMode = false
                    categorySpinner.isEnabled = true
                    loadTopHeadlines(currentCategory)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun loadTopHeadlines(category: String) {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.apiService.getTopHeadlines(category = category)
                newsAdapter.submitList(response.articles)
                if (response.articles.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Новостей не найдено по категории $category", Toast.LENGTH_SHORT).show()
                }
                swipeRefreshLayout.isRefreshing = false
            } catch (e: Exception) {
                Log.e("MainActivity", "Ошибка загрузки: ${e.message}")
                Toast.makeText(this@MainActivity, "Ошибка загрузки: ${e.message}", Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            } finally {
                showLoading(false)
            }
        }
    }

    private fun searchNews(query: String) {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.apiService.searchNews(query)
                newsAdapter.submitList(response.articles)
                if (response.articles.isEmpty()) {
                    Toast.makeText(this@MainActivity, "По запросу '$query' ничего не найдено", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Ошибка поиска: ${e.message}")
                Toast.makeText(this@MainActivity, "Ошибка поиска: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        newsRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
}