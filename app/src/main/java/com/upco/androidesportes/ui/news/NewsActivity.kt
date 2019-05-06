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
import com.upco.androidesportes.R
import com.upco.androidesportes.databinding.ActivityNewsBinding
import com.upco.androidesportes.model.News
import com.upco.androidesportes.util.Injector
import kotlinx.android.synthetic.main.app_bar_news.*

class NewsActivity: AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel
    private val adapter = NewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Faz binding do layout */
        val binding = DataBindingUtil.setContentView<ActivityNewsBinding>(
            this,
            R.layout.activity_news
        )

        /* Define o título da Toolbar e a define como supportActionBar */
        setupToolbar()

        /*
         * Obtém o ViewModel e configura um Observer que notificará o
         * adapter sempre que houver alterações no dados do ViewModel (news)
         */
        setupViewModel()

        /* Faz binding do ViewModel com o código estático */
        binding.viewModel = viewModel

        /* Define o adapter do RecyclerView */
        binding.rvNews.adapter = adapter

        // TODO: mover para um Service
        /* Faz a requisição inicial pelas notícias */
        viewModel.fetchNews()
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
     * Configura a Toolbar.
     */
    private fun setupToolbar() {
        toolbar.title = getString(R.string.news_activity_title)
        setSupportActionBar(toolbar)
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
        })

        /* Define um Observer para observar às alterações em networkErrors */
        viewModel.networkErrors.observe(this, Observer<String> {
            Toast.makeText(this, "\uD8D3\uDE28 Ooops $it", Toast.LENGTH_LONG)
                 .show()
        })
    }

    /**
     * Esse método é chamado caso o item de "Configurações", no menu, seja selecionado.
     */
    private fun handleActionSettings(): Boolean {
        Toast.makeText(this, "Configurações", Toast.LENGTH_SHORT)
             .show()
        return true
    }
}