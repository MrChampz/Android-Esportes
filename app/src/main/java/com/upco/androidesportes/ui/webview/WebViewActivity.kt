package com.upco.androidesportes.ui.webview

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.upco.androidesportes.R
import com.upco.androidesportes.model.News
import com.upco.androidesportes.ui.common.BaseActivity
import com.upco.androidesportes.ui.common.showWithAnimation
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.activity_webview.fab_top
import kotlinx.android.synthetic.main.app_bar_webview.*

/**
 * Exibe a página da notícia em um [WebView].
 */
class WebViewActivity: BaseActivity() {

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
        setupToolbar(news.title, true)

        /* Configura o WebView */
        setupWebView()

        /* Configura o FloatingActionButton */
        setupFloatingActionButton()
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

    /**
     * Configura o FloatingActionButton.
     */
    private fun setupFloatingActionButton() {
        /* O estado inicial do FAB deve ser oculto */
        fab_top.hide()

        /*
         * Adiciona um listener ao NestedScrollView que será acionado sempre que houver uma rolagem.
         * Isso é necessário pois queremos que o FAB seja exibido apenas depois de certa rolagem.
         */
        nsv_news.setOnScrollChangeListener(object: NestedScrollView.OnScrollChangeListener {

            /*
             * Indica se o FAB está ativo ou não.
             * Essa variável controla quando o FAB deve ser exibido ou ocultado por esse listener.
             * Isso é necessário pois o FABScrollBehavior também controla quando o FAB é ou não
             * exibido.
             */
            private var activated = false

            /**
             * Chamado quando a posição de rolagem de uma view muda.
             *
             * @param v          A view da qual a posição de rolagem mudou.
             * @param scrollX    Origem atual da rolagem horizontal.
             * @param scrollY    Origem atual da rolagem vertical.
             * @param oldScrollX Origem anterior da rolagem horizontal.
             * @param oldScrollY Origem anterior da rolagem vertical.
             */
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                /*
                 * Exibe o FAB apenas se a rolagem passar de certo ponto (800 pixels), antes
                 * desse ponto ele ficará oculto.
                 * Primeiro verifica se a rolagem vertical consumiu mais de 800 pixels e se o
                 * FAB está inativo e se sim, ativa e exibe ele.
                 * Caso não passe nas condições, verifica se a rolagem vertical consumiu menos
                 * que ou 800 pixels e se o FAB está ativo, e nesse caso desativa e oculta ele.
                 */
                if (scrollY > 800 && !activated) {
                    /* Ativa o FAB */
                    activated = true

                    /*
                     * Exibe o FAB.
                     * A função showWithAnimation(), que estende a classe FloatingActionButton,
                     * é usada aqui para contornar um bug em que a animação do FAB não funciona
                     * de primeira, quando a activity é criada.
                     */
                    fab_top.showWithAnimation()
                } else if (scrollY <= 800 && activated){
                    /* Desativa o FAB */
                    activated = false

                    /* Oculta o FAB */
                    fab_top.hide()
                }
            }
        })

        /* Define o listener que será acionado quando o FAB receber um clique */
        fab_top.setOnClickListener {
            /* Faz com que o NestedScrollView role suavemente até a posição inicial da página */
            nsv_news.smoothScrollTo(0, 0)
        }
    }
}