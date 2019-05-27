package com.upco.androidesportes.ui.news

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.upco.androidesportes.R
import com.upco.androidesportes.databinding.ActivityNewsBinding
import com.upco.androidesportes.model.News
import com.upco.androidesportes.util.Injector
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.app_bar_news.*

class NewsActivity: AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

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
        setupToolbar()

        /* Configura o SwipeRefreshLayout */
        setupSwipeRefreshLayout()

        /* Configura o ViewModel */
        setupViewModel()

        /* Faz binding do ViewModel com o código estático */
        binding.viewModel = viewModel

        /* Define o adapter do RecyclerView */
        rv_news.adapter = adapter

        /* Faz a requisição inicial pelas notícias */
        requestInitialData()
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
        viewModel.refreshNews()
    }

    /**
     * Configura a Toolbar.
     */
    private fun setupToolbar() {
        /* Define o título da Toolbar */
        toolbar.title = getString(R.string.news_activity_title)

        /* Define a Toolbar como supportActionBar dessa activity */
        setSupportActionBar(toolbar)
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
     * Configura o ViewModel.
     */
    private fun setupViewModel() {
        /* Obtém o ViewModel */
        viewModel = ViewModelProviders
                .of(this, Injector.provideNewsViewModelFactory(this))
                .get(NewsViewModel::class.java)

        /* Define um Observer para observar às alterações em news */
        viewModel.news.observe(this, Observer<PagedList<News>> {
            /*
             * Se houver alguma alteração na lista de notícias,
             * passa a lista alterada para o adapter.
             */
            adapter.submitList(it)

            /* Se os dados foram recebidos, o feed não está em atualização mais */
            srl_news.isRefreshing = it.isEmpty()
        })

        /* Define um Observer para observar às alterações em networkErrors */
        viewModel.networkErrors.observe(this, Observer<String> {
            Toast.makeText(this, "\uD8D3\uDE28 Ooops $it", Toast.LENGTH_LONG)
                 .show()
        })
    }

    /**
     * Faz a requisição inicial pelas notícias.
     * Esse método deve ser chamado sempre que o app for aberto.
     */
    private fun requestInitialData() {
        /* Indica ao SwipeRefreshLayout que estamos atualizando o feed */
        srl_news.isRefreshing = true

        /* Faz a requisição inicial pelas notícias. */
        viewModel.fetchNews()
    }

    /**
     * TODO: Implementar
     * Esse método é chamado caso o item de "Configurações", no menu, seja selecionado.
     */
    private fun handleActionSettings(): Boolean {
        Toast.makeText(this, "Configurações", Toast.LENGTH_SHORT)
             .show()
        return true
    }
}