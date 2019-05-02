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

    private val news = listOf(
        News(
            null,
            "Reforma trabalhista",
            "Ministro do STF suspende norma que permite grávidas em atividade insalubre",
            "Alexandre de Moraes entendeu que a proteção à maternidade é direito irrenunciável. Tema agora será analisado pelo plenário do Supremo.",
            "",
            10263
        ),
        News(
            "http://i.s3.glbimg.com/v1/AUTH_59edd422c0c84a879bd37670ae4f538a/internal_photos/bs/2019/R/a/qcjXilRYqLBU9E5vOwBw/oposicao.jpg",
            "Venezuela",
            "Guaidó promete protestos diários; chavistas também se reúnem",
            "Opositor disse que mobilização de ontem contra Maduro 'não foi suficiente'; há confrontos.",
            "",
            14722
        ),
        News(
            "http://i.s3.glbimg.com/v1/AUTH_59edd422c0c84a879bd37670ae4f538a/internal_photos/bs/2019/f/r/mBL60ET3SbBh5Qhs2PTg/2019-01-14t122352z-711532144-rc1394b28380-rtrmadp-3-russia-japan-lavrov.jpg",
            null,
            "Chanceler russo adverte EUA sobre 'passos agressivos' na Venezuela",
            "Secretário de Estado dos EUA acusa a Rússia de ajudar o regime de Maduro.",
            "",
            4319
        ),
        News(
            "https://s2.glbimg.com/tSEjWpxfWagIhMdZsrIOiQjh-yc=/1200x/smart/filters:cover():strip_icc()/s01.video.glbimg.com/x720/7581808.jpg",
            null,
            "'Não tem derrota', diz Bolsonaro após ação frustrada de Guaidó",
            "Comandantes das Forças Armadas foram chamados para encontro em Brasília.",
            "",
            19050
        ),
        News(
            "http://s01.video.glbimg.com/x720/7582352.jpg",
            null,
            "Entrada de venezuelanos no Brasil dobra com novos confrontos",
            "Só ontem, quase 900 cruzaram a fronteira por rotas clandestinas em Roraima.",
            "",
            19050
        ),
        News(
            "http://i.s3.glbimg.com/v1/AUTH_59edd422c0c84a879bd37670ae4f538a/internal_photos/bs/2019/2/M/avr0RcTMW2mL8hVsAw0Q/whatsapp-image-2019-05-01-at-15.58.24.jpeg",
            "'Madrinha do samba'",
            "Despedida de Beth Carvalho tem roda de samba, coro e cortejo",
            null,
            "",
            3866
        ),
        News(
            "http://i.s3.glbimg.com/v1/AUTH_59edd422c0c84a879bd37670ae4f538a/internal_photos/bs/2019/H/h/uBplc6RAK7uewmnPxEJw/zeca-pagodinho.jpg",
            null,
            "'Virei Zeca Pagodinho por causa da Beth Carvalho', diz sambista",
            "Hino do Botafogo e sambas históricos foram cantados no velório",
            "",
            21266
        )
    )

    private lateinit var viewModel: NewsViewModel
    //private val adapter = NewsAdapter()
    private val adapter = NewsTestAdapter(news)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Faz binding do layout
        val binding = DataBindingUtil.setContentView<ActivityNewsBinding>(
            this,
            R.layout.activity_news
        )

        // Define o título da Toolbar e a define como supportActionBar
        setupToolbar()

        // Obtem o ViewModel e configura um Observer que notificará o
        // adapter sempre que houver alterações no dados do ViewModel (news)
        setupViewModel()

        // Faz binding do ViewModel com o código estático
        binding.viewModel = viewModel

        // Define o adapter do RecyclerView
        binding.rvNews.adapter = adapter
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
        // Obtem o ViewModel
        viewModel = ViewModelProviders
                .of(this, Injector.provideNewsViewModelFactory(this))
                .get(NewsViewModel::class.java)

        // Define um Observer para observar às alterações em news
        viewModel.news.observe(this, Observer<PagedList<News>> {
            // Se houver alguma alteração na lista de notícias,
            // passa a lista alterada para o adapter.
            // TODO: Descomentar isso
            //adapter.submitList(it)
        })

        // Define um Observer para observar às alterações em networkErrors
        viewModel.networkErrors.observe(this, Observer<String> {
            Toast.makeText(this, "\uD8D3\uDE28 Wooops $it", Toast.LENGTH_LONG)
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