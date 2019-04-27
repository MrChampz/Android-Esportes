package com.upco.androidesportes.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upco.androidesportes.databinding.ListItemNewsBinding
import com.upco.androidesportes.model.News

/**
 * ViewHolder para a classe de modelo [News].
 */
class NewsViewHolder(private val binding: ListItemNewsBinding):
        RecyclerView.ViewHolder(binding.root) {

    fun bind(news: News) {
        binding.news = news
        binding.executePendingBindings()
    }

    companion object {
        fun create(parent: ViewGroup): NewsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemNewsBinding.inflate(inflater, parent, false)
            return NewsViewHolder(binding)
        }
    }
}