package com.upco.androidesportes.ui.news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.upco.androidesportes.model.News
import com.upco.androidesportes.ui.webview.WebViewActivity

/**
 * Classe que fornece métodos handler, que são chamados a partir de alguma ação na UI.
 */
class BindingHandlers(private val activity: AppCompatActivity) {

    /**
     * Handler chamado ao clicar em uma notícia no RecyclerView.
     * Instancia a [WebViewActivity] e passa a notícia ([news]) por intent.
     * Dessa forma, o conteúdo da notícia é exibido por meio de sua url,
     * que é carregada no WebView.
     *
     * @param news A notícia que recebeu o clique.
     */
    fun onClickNews(news: News) {
        /* Cria o intent para a WebViewActivity */
        val intent = Intent(activity, WebViewActivity::class.java)

        /* Passa a notícia por intent */
        intent.putExtra(News.KEY, news)

        /* Inicia a WebViewActivity */
        activity.startActivity(intent)
    }
}