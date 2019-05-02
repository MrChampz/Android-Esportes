package com.upco.androidesportes.ui.news

import android.app.Activity
import android.widget.Toast
import com.upco.androidesportes.model.News

/**
 * Classe que fornece métodos handler, que são chamados a partir de alguma ação na UI.
 */
class BindingHandlers(val activity: Activity) {

    /**
     * Handler chamado ao clicar em um item (notícia) no RecyclerView.
     */
    fun onClickNews(news: News) {
        Toast.makeText(activity, "Notícia", Toast.LENGTH_SHORT)
             .show()
    }
}