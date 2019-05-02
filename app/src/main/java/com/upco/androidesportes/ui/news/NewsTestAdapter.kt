package com.upco.androidesportes.ui.news

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upco.androidesportes.model.News

class NewsTestAdapter(private val news: List<News>): RecyclerView.Adapter<NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        return holder.bind(news[position])
    }

    override fun getItemCount() = news.size
}