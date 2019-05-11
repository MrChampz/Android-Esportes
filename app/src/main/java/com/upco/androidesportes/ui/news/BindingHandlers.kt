package com.upco.androidesportes.ui.news

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.upco.androidesportes.model.News

/**
 * Classe que fornece métodos handler, que são chamados a partir de alguma ação na UI.
 */
class BindingHandlers(private val activity: AppCompatActivity) {

    /**
     * Handler chamado ao clicar em um item (notícia) no RecyclerView.
     */
    fun onClickNews(news: News) {
        Toast.makeText(activity, "Notícia", Toast.LENGTH_SHORT)
             .show()
    }
}