package com.upco.androidesportes.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.upco.androidesportes.databinding.ListItemNewsBinding
import com.upco.androidesportes.model.News
import com.upco.androidesportes.util.DateUtils

/**
 * ViewHolder para a classe de modelo [News].
 */
class NewsViewHolder(
    private val activity: AppCompatActivity,
    private val binding: ListItemNewsBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(news: News) {
        /* Configura os handlers */
        binding.handlers = BindingHandlers(activity)

        /* Passa a notícia para o código estático */
        binding.news = news
        binding.executePendingBindings()
    }

    companion object {
        fun create(activity: AppCompatActivity, parent: ViewGroup): NewsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemNewsBinding.inflate(inflater, parent, false)
            return NewsViewHolder(activity, binding)
        }
    }
}