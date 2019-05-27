package com.upco.androidesportes.ui.webview

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.upco.androidesportes.R
import com.upco.androidesportes.model.News
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.app_bar_webview.*

class WebViewActivity: AppCompatActivity() {

    /*
     * Referencia a notícia passada por intent, pois essa
     * será utilizada nos vários métodos da activity.
     */
    private lateinit var news: News

    /**
     * Método chamado quando a activity está iniciando,
     * é aqui que o código de inicialização deve ir.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Define o layout dessa activity */
        setContentView(R.layout.activity_webview)

        /* Pega a notícia passada por intent, quando essa activity é instanciada */
        news = intent.getParcelableExtra(News.KEY)

        /* Configura a Toolbar */
        setupToolbar()

        /* Configura o WebView */
        setupWebView()
    }

    /**
     * Configura a Toolbar.
     */
    private fun setupToolbar() {
        /* Define o título da Toolbar, de acordo com o título da notícia */
        toolbar.title = news.title

        /*
         * Define o que deve ser feito ao clicar no ícone de navegação.
         * Nesse caso, a activity será encerrada e a NewsActivity será exibida novamente.
         */
        toolbar.setNavigationOnClickListener {
            finish()
        }

        /* Define a Toolbar como supportActionBar dessa activity */
        setSupportActionBar(toolbar)
    }

    /**
     * Configura o WebView.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        /* Habilita o JavaScript no WebView */
        wv_news.settings.javaScriptEnabled = true

        /* Define o [WebViewClient] que receberá as notificações e requisições */
        wv_news.webViewClient = object: WebViewClient() {

            /**
             * Permite que a aplicação controle como as URLs serão redirecionadas
             * a partir da página.
             *
             * @param view O [WebView] que iniciou o callback.
             * @param url  URL a ser carregada.
             * @return true para cancelar o carregamento da URL, caso contrário retorne false.
             */
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String
            ) = loadUrlOnBrowser(Uri.parse(url))

            /**
             * Permite que a aplicação controle como as URLs serão redirecionadas
             * a partir da página.
             *
             * @param view    O [WebView] que iniciou o callback.
             * @param request Objeto contendo os detalhes da requisição.
             * @return true para cancelar o carregamento da URL, caso contrário retorne false.
             */
            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest
            ) = loadUrlOnBrowser(request.url)

            /**
             * Cria um intent para abrir as URLs redirecionadas no navegador.
             *
             * @param url URL a ser carregada
             * @return Retorna true indicando que o redirecionamento será tratado
             *         pela aplicação, e que a URL não deve ser carregada no [WebView].
             */
            private fun loadUrlOnBrowser(url: Uri): Boolean {
                /* Cria o intent responsável por abrir a URL no navegador */
                val intent = Intent(Intent.ACTION_VIEW)

                /* Passa a URL para o intent */
                intent.data = url

                /* Inicia o intent */
                startActivity(intent)

                /*
                 * Retorna true indicando que o redirecionamento será tratado
                 * pela aplicação, e que a URL não deve ser carregada no WebView.
                 */
                return true
            }
        }

        /*
         * Define o [WebChromeClient], que é responsável por tratar diálogos JS, favicons,
         * títulos, e o progresso de carregamento da página.
         */
        wv_news.webChromeClient = object: WebChromeClient() {

            /**
             * Diz à aplicação o progresso atual do carregamento da página.
             *
             * @param view        O WebView que iniciou o callback.
             * @param newProgress O progresso atual do carregamento da página,
             *                    representado por um inteiro entre 0 e 100.
             */
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                /* Define o novo progresso no ProgressBar  */
                pb_progress.progress = newProgress

                /* Se a página for totalmente carregada, retira o ProgressBar da activity */
                if (newProgress == 100) {
                    pb_progress.visibility = ProgressBar.GONE
                }
            }
        }

        /* Por fim, carrega a página da notícia por meio de sua url */
        wv_news.loadUrl(news.url)
    }
}