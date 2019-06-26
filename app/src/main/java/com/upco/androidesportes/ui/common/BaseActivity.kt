package com.upco.androidesportes.ui.common

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.upco.androidesportes.R

/**
 * Activity que serve de base para todas as outras. Todos os métodos comuns à todas as
 * activities são implementados aqui.
 */
@SuppressLint("Registered")
open class BaseActivity:
        AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener {

    /* Recebe o estado atual da rede, sempre que houver alguma mudança */
    private lateinit var networkStateReceiver: NetworkStateReceiver

    /*
     * Indica se a activity acabou de ser criada ou não.
     * Essa variável é necessária, pois é por meio dela que controlamos se o
     * Snackbar deve ser exibido ou não, pois ele não deve ser exibido logo
     * ao criar a activity.
     */
    private var createdNow = true

    /**
     * Método chamado quando a activity está iniciando,
     * é aqui que o código de inicialização deve ir.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Configura o NetworkStateReceiver */
        setupNetworkStateReceiver()
    }

    /**
     * Método chamado quando a activity está sendo destroída.
     */
    override fun onDestroy() {
        super.onDestroy()

        /* Remove a activity da lista de listeners */
        networkStateReceiver.removeListener(this)

        /* Faz com que a activity deixe de receber atualizações do estado da rede */
        unregisterReceiver(networkStateReceiver)
    }

    /**
     * Esse método é chamado quando há uma alteração de rede e há uma conexão.
     * Exibe um [Snackbar] indicando ao usuário que há rede conectada.
     * NOTA: O [Snackbar] não é exibido na criação da activity.
     */
    override fun onNetworkAvailable() {
        /* Loga para fins de debug */
        Log.d(TAG, "Rede disponível")

        /*
         * Verifica se a activity acabou de ser criada, pois o Snackbar
         * não deve ser exibido nesse caso.
         */
        if (!createdNow) {
            Snackbar.make(
                findViewById(android.R.id.content),
                R.string.tx_network_available,
                Snackbar.LENGTH_LONG
            ).show()
        }

        /*
         * Define como false, indicando que a activity não acabou de ser criada
         * e, portanto, das próximas vezes que esse método for chamado o Snackbar
         * será exibido normalmente.
         */
        if (createdNow) createdNow = false
    }

    /**
     * Esse método é chamado quando há uma alteração de rede e não há uma conexão.
     * Exibe um [Snackbar] indicando ao usuário que não há rede conectada.
     * NOTA: O [Snackbar] não é exibido na criação da activity.
     */
    override fun onNetworkUnavailable() {
        /* Loga para fins de debug */
        Log.d(TAG, "Rede indisponível")

        /*
         * Verifica se a activity acabou de ser criada, pois o Snackbar
         * não deve ser exibido nesse caso.
         */
        if (!createdNow) {
            Snackbar.make(
                findViewById(android.R.id.content),
                R.string.tx_network_unavailable,
                Snackbar.LENGTH_LONG
            ).show()
        }

        /*
         * Define como false, indicando que a activity não acabou de ser criada
         * e, portanto, das próximas vezes que esse método for chamado o Snackbar
         * será exibido normalmente.
         */
        if (createdNow) createdNow = false
    }

    /**
     * Configura a [Toolbar].
     *
     * @param title         Título da [Toolbar].
     * @param hasNavigation Indica se a [Toolbar] tem a ação de navegação (voltar).
     */
    protected fun setupToolbar(title: String?, hasNavigation: Boolean) {
        /* Referencia a Toolbar por meio de seu id */
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        /* Define o título da Toolbar. Se title for nulo, define como "". */
        toolbar.title = title ?: ""

        /* Define a Toolbar como supportActionBar dessa activity */
        setSupportActionBar(toolbar)

        /* Verifica se a Toolbar tem a ação de navegação (voltar) */
        if (hasNavigation) {
            /*
             * Define o que deve ser feito ao clicar no ícone de navegação.
             * Nesse caso, a activity será encerrada e a activity anterior
             * será exibida novamente.
             */
            toolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    /**
     * Configura o [NetworkStateReceiver].
     */
    private fun setupNetworkStateReceiver() {
        /*
         * Instancia o NetworkStateReceiver.
         * DEVE ser instanciado no onCreate ou após ele, pois o contexto
         * só fica disponível com a activity já criada.
         */
        networkStateReceiver = NetworkStateReceiver(this)

        /* Adiciona a activity na lista de listeners */
        networkStateReceiver.addListener(this)

        /*
         * Registra o NetworkStateReceiver com a activity.
         * Faz com que a activity receba atualizações do estado da rede.
         */
        registerReceiver(networkStateReceiver, IntentFilter(CONNECTIVITY_ACTION))
    }

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
    }
}