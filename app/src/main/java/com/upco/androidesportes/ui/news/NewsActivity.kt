package com.upco.androidesportes.ui.news

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.upco.androidesportes.R
import com.upco.androidesportes.databinding.ActivityNewsBinding
import com.upco.androidesportes.model.News
import com.upco.androidesportes.ui.common.BaseActivity
import com.upco.androidesportes.ui.common.showWithAnimation
import com.upco.androidesportes.ui.settings.SettingsActivity
import com.upco.androidesportes.util.Injector
import com.upco.androidesportes.util.NetworkUtils
import com.upco.androidesportes.util.PreferenceUtils
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.app_bar_news.*

/**
 * Exibe um feed de notícias. Ao clicar em alguma, instancia uma [WebViewActivity] para exibi-la.
 */
class NewsActivity: BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var viewModel: NewsViewModel
    private val adapter = NewsAdapter(this)

    /**
     * Método chamado quando a activity está iniciando,
     * é aqui que o código de inicialização deve ir.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Faz binding do layout */
        val binding = DataBindingUtil.setContentView<ActivityNewsBinding>(
            this,
            R.layout.activity_news
        )

        /* Configura a Toolbar */
        setupToolbar(getString(R.string.news_activity_title), false)

        /* Configura o SwipeRefreshLayout */
        setupSwipeRefreshLayout()

        /* Configura o FloatingActionButton */
        setupFloatingActionButton()

        /* Configura o ViewModel */
        setupViewModel()

        /* Faz binding do ViewModel com o código estático */
        binding.viewModel = viewModel

        /* Define o adapter do RecyclerView */
        rv_news.adapter = adapter

        /* Faz a requisição inicial pelas notícias */
        requestInitialNews()
    }

    /**
     * Infla o menu de opções da activity.
     * <p>
     * Nesse caso, o menu contém apenas o item de "Configurações".
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.news, menu)
        return true
    }

    /**
     * Reage à seleção de algum item do menu.
     * <p>
     * Nesse caso, é tratado apenas do caso em que o item corresponda a
     * R.id.action_settings (Configurações), pois é o unico item no menu dessa activity.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> handleActionSettings()
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Método chamado quando o usuário desliza o [SwipeRefreshLayout],
     * para que o conteúdo do feed seja atualizado.
     */
    override fun onRefresh() {
        /* Indica ao ViewModel que queremos atualizar o feed de notícias */
        refreshNews()
    }

    /**
     * Configura o SwipeRefreshLayout.
     */
    private fun setupSwipeRefreshLayout() {
        /* Define o listener que será chamado quando atualizando */
        srl_news.setOnRefreshListener(this)

        /* Define o esquema de cores que serão usadas, nesse caso apenas a cor primária do app */
        srl_news.setColorSchemeResources(R.color.colorPrimary)
    }

    /**
     * Configura o FloatingActionButton.
     */
    private fun setupFloatingActionButton() {
        /* O estado inicial do FAB deve ser oculto */
        fab_top.hide()

        /*
         * Adiciona um listener ao RecyclerView que será acionado sempre que houver uma rolagem.
         * Isso é necessário pois queremos que o FAB seja exibido apenas depois de certa rolagem.
         */
        rv_news.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            /*
             * Indica se o FAB está ativo ou não.
             * Essa variável controla quando o FAB deve ser exibido ou ocultado por esse listener.
             * Isso é necessário pois o FABScrollBehavior também controla quando o FAB é ou não
             * exibido.
             */
            private var activated = false

            /**
             * Método callback invocado quando o RecyclerView é rolado. Esse método é chamado
             * após a rolagem.
             *
             * @param recyclerView O RecyclerView que sofreu a rolagem.
             * @param dx           A quantidade de rolagem horizontal.
             * @param dy           A quantidade de rolagem vertical.
             */
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                /* Pega o LayoutManager do RecyclerView */
                val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)

                /* Pega a posição do primeiro item visível atualmente no RecyclerView */
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                /*
                 * Exibe o FAB apenas se a rolagem passar de 3 itens no RecyclerView, antes
                 * desse ponto ele ficará oculto.
                 * Verifica se a posição é maior do que 2 e se o FAB está inativo e se sim,
                 * ativa e exibe ele.
                 * Caso não passe nas condições, verifica se a posição é menor ou igual a 2
                 * e se o FAB está ativo, e nesse caso desativa e oculta ele.
                 */
                if (firstVisibleItem > 2 && !activated) {
                    /* Ativa o FAB */
                    activated = true

                    /*
                     * Exibe o FAB.
                     * A função showWithAnimation(), que estende a classe FloatingActionButton,
                     * é usada aqui para contornar um bug em que a animação do FAB não funciona
                     * de primeira, quando a activity é criada.
                     */
                    fab_top.showWithAnimation()
                } else if (firstVisibleItem <= 2 && activated) {
                    /* Desativa o FAB */
                    activated = false

                    /* Oculta o FAB */
                    fab_top.hide()
                }
            }
        })

        /* Define o listener que será acionado quando o FAB receber um clique */
        fab_top.setOnClickListener {
            /* Faz com que o RecyclerView de notícias role suavemente até a posição inicial */
            rv_news.smoothScrollToPosition(0)
        }
    }

    /**
     * Configura o ViewModel.
     */
    private fun setupViewModel() {
        /* Obtém o ViewModel */
        viewModel = ViewModelProviders
                .of(this, Injector.provideNewsViewModelFactory(this))
                .get(NewsViewModel::class.java)

        /* Define um Observer para observar às alterações em news */
        viewModel.news.observe(this, Observer<PagedList<News>> {
            /* Verifica se os dados foram recebidos */
            if (!it.isEmpty()) {
                /* Passa a lista alterada para o adapter */
                adapter.submitList(it)

                // TODO: Refatorar! Está voltando ao topo sempre que o feed é carregado com mais
                //  notícias, ao fazer scroll.
                /* Faz com que o RecyclerView de notícias role suavemente até a posição inicial */
                //rv_news.smoothScrollToPosition(0)

                /* Os dados foram recebidos, o feed não está em atualização mais */
                if (srl_news.isRefreshing)
                    srl_news.isRefreshing = false
            }
        })

        /* Define um Observer para observar às alterações em networkErrors */
        viewModel.networkErrors.observe(this, Observer<String> {
            Toast.makeText(this, "\uD8D3\uDE28 Ooops $it", Toast.LENGTH_LONG)
                 .show()
        })
    }

    /**
     * Esse método é chamado caso o item de "Configurações", no menu, seja selecionado.
     * Ele é responsável por iniciar a activity de configurações.
     *
     * @return Retorna true indicando que a ação foi tratada.
     */
    private fun handleActionSettings(): Boolean {
        /* Cria o intent para a activity */
        val intent = Intent(this, SettingsActivity::class.java)

        /* Inicia a activity por meio do intent */
        startActivity(intent)

        /* Retorna true indicando que a ação foi tratada */
        return true
    }

    /**
     * Faz a requisição inicial pelas notícias.
     * Esse método deve ser chamado sempre que o app for aberto.
     */
    private fun requestInitialNews() {
        /* Indica ao SwipeRefreshLayout que estamos atualizando o feed */
        srl_news.isRefreshing = true

        /* Faz a requisição inicial pelas notícias. */
        viewModel.fetchNews()

        /* Faz com que os dados em cache sejam atualizados para os mais recentes na API. */
        refreshNews()
    }

    /**
     * Atualiza as notícias em cache, com as mais recentes na API, se houver internet.
     * Esse método é chamado na atualização do feed.
     */
    private fun refreshNews() {
        /*
         * Obtém o valor da configuração que indica se os dados devem ser
         * baixados apenas por WiFi ou por redes móveis também.
         * O valor de retorno true indica que ambas as redes podem ser usadas.
         */
        val useBothNetworks = PreferenceUtils.shouldUseBothNetworks(this)

        /* Verifica se o WiFi está conectado */
        val isWifiConnected = NetworkUtils.isWifiConnected(this)

        /* Verifica se a rede móvel está conectada */
        val isMobileConnected = NetworkUtils.isMobileConnected(this)

        /* Indica se os dados locais devem ser atualizados de acordo com a API */
        val fetchNewData = if (isWifiConnected) {
            /* Loga para fins de debug */
            Log.d(TAG, "O dados serão baixados por WiFi.")

            /* Retorna true, indicando que as notícias devem ser atualizadas */
            true
        } else if (useBothNetworks && isMobileConnected) {
            /* Loga para fins de debug */
            Log.d(TAG, "O dados serão baixados por rede móvel.")

            /* Retorna true, indicando que as notícias devem ser atualizadas */
            true
        } else if (!useBothNetworks && isMobileConnected) {
            /* Loga para fins de debug */
            Log.d(TAG, "Há rede móvel, porém os dados só podem ser baixados por WiFi. " +
                    "Os dados não serão baixados.")

            /* Notifica o erro ao usuário por meio de um Snackbar */
            Snackbar.make(
                findViewById(android.R.id.content),
                "Erro ao atualizar o feed. Os dados devem ser baixados por WiFi.",
                Snackbar.LENGTH_LONG
            ).show()

            /* Retorna false, indicando que as notícias não devem ser atualizadas */
            false
        } else {
            /* Loga para fins de debug */
            Log.d(TAG, "Não há qualquer tipo de rede. Os dados não serão baixados.")

            /* Notifica o erro ao usuário por meio de um Snackbar */
            Snackbar.make(
                findViewById(android.R.id.content),
                "Erro ao atualizar o feed. Não há nenhuma rede conectada.",
                Snackbar.LENGTH_LONG
            ).show()

            /* Retorna false, indicando que as notícias não devem ser atualizadas */
            false
        }

        /* Atualiza as notícias de acordo com a API */
        if (fetchNewData) {
            viewModel.refreshNews()
        }
    }

    companion object {
        private val TAG = NewsActivity::class.java.simpleName
    }
}