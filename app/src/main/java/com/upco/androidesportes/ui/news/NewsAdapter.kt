package com.upco.androidesportes.ui.news

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upco.androidesportes.model.News

/**
 * Adapter para a lista de notícias.
 */
class NewsAdapter(private val activity: AppCompatActivity):
        PagedListAdapter<News, RecyclerView.ViewHolder>(NEWS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NewsViewHolder.create(activity, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val news = getItem(position)
        if (news != null) {
            (holder as NewsViewHolder).bind(news)
        }
    }

    companion object {
        private val NEWS_COMPARATOR = object: DiffUtil.ItemCallback<News>() {

            override fun areItemsTheSame(oldItem: News, newItem: News) =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: News, newItem: News) =
                    oldItem == newItem
        }
    }
}