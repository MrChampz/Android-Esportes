package com.upco.androidesportes.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.upco.androidesportes.databinding.ListItemNewsBinding
import com.upco.androidesportes.model.News

/**
 * ViewHolder para a classe de modelo [News].
 *
 * @param activity Activity utilizada para inicializar os handlers.
 * @param binding  Binding do layout da notícia.
 */
class NewsViewHolder(
    private val activity: AppCompatActivity,
    private val binding: ListItemNewsBinding
): RecyclerView.ViewHolder(binding.root) {

    /**
     * Faz binding da notícia [news] com o layout.
     *
     * @param news Notícia a ser exibida.
     */
    fun bind(news: News) {
        /* Configura os handlers */
        binding.handlers = BindingHandlers(activity)

        /* Passa a notícia para o código estático */
        binding.news = news
        binding.executePendingBindings()
    }

    companion object {
        /**
         * Cria e retorna uma instância de [NewsViewHolder].
         *
         * @param activity Activity utilizada para inicializar os handlers.
         * @param parent   ViewGroup utilizado para inflar a view.
         * @return Instância de [NewsViewHolder]
         */
        fun create(activity: AppCompatActivity, parent: ViewGroup): NewsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemNewsBinding.inflate(inflater, parent, false)
            return NewsViewHolder(activity, binding)
        }
    }
}