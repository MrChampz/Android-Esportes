package com.upco.androidesportes.ui.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log

/**
 * [NetworkStateReceiver] define um [BroadcastReceiver] que nos permite registrar
 * eventos do sistema (ex. estado da rede) ou da aplicação. Todos os receptores
 * registrados para um evento serão notificados assim que esse evento acontecer.
 *
 * Fonte:
 * http://stackoverflow.com/questions/6169059/android-event-for-internet-connectivity-state-change
 */
class NetworkStateReceiver(context: Context): BroadcastReceiver() {

    /* Será usado para verificar o estado da rede */
    private val manager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    /*
     * Armazena os listeners que serão notificados assim que houver uma
     * mudança no estado da rede.
     */
    private val listeners = arrayListOf<NetworkStateReceiverListener>()

    /* Indica se há alguma rede conectada */
    private var connected: Boolean? = null

    /**
     * Chamado quando o [BroadcastReceiver] recebe um Intent broadcast (quando o evento para o
     * qual o receptor foi registrado acontece).
     *
     * NOTA: Quando executado na thread principal, nunca deve ser feitas operações que demorem
     * (há um limite de 10 segundos que o sistema permite antes de considerar que o receptor
     * está bloqueado).
     *
     * NOTA: Não é possível iniciar um diálogo popup no [onReceive].
     *
     * @param context Objeto para acessar informações adicionais ou para iniciar
     *                serviços e activities.
     * @param intent  Objeto com a ação usada para registrar o receptor. Contém informações
     *                adicionais (ex. extras).
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        /* Loga para fins de debug */
        Log.d(TAG, "Intent broadcast recebido")

        /* Se o intent ou o extras for nulo, para a execução do método */
        if (intent == null || intent.extras == null) return

        /*
         * Pega os detalhes da rede conectada atualmente.
         * Caso não haja nenhuma rede conectada, retorna nulo.
         */
        val networkInfo = manager.activeNetworkInfo

        /*
         * Verifica se networkInfo não é nulo e se o estado da rede é CONECTADA, se sim,
         * define a variável connected como true, indicando que há alguma rede conectada.
         * Caso contrário, verifica pelas informações do intent se não há conexão, se de
         * fato não houver, define a variável connected como false, indicando que não há
         * nenhuma rede conectada.
         */
        if (
            networkInfo != null
            && networkInfo.detailedState == NetworkInfo.DetailedState.CONNECTED
        ) {
            connected = true
        } else if (
            intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
        ) {
            connected = false
        }

        /* Notifica o estado da rede à todos os listeners registrados */
        notifyStateToAll()
    }

    /**
     * Adiciona o [listener] à lista, assim ele será notificado quando houver
     * alterações no estado da rede.
     *
     * @param listener Objeto que implemente a interface [NetworkStateReceiverListener].
     */
    fun addListener(listener: NetworkStateReceiverListener) {
        /* Loga para fins de debug */
        Log.d(TAG, "Adicionando novo listener")

        /* Adiciona o listener */
        listeners.add(listener)

        /* Notifica o estado da rede para o listener */
        notifyState(listener)
    }

    /**
     * Remove o [listener] (quando não mais necessário) da lista, assim ele não será mais
     * notificado quando houver alterações no estado da rede.
     *
     * @param listener Objeto que implemente a interface [NetworkStateReceiverListener].
     */
    fun removeListener(listener: NetworkStateReceiverListener) {
        /* Loga para fins de debug */
        Log.d(TAG, "Removendo listener")

        /* Remove o listener */
        listeners.remove(listener)
    }

    /**
     * Notifica o estado da rede à todos os listeners registrados.
     */
    private fun notifyStateToAll() {
        /* Loga para fins de debug */
        Log.d(TAG, "Notificando estado da rede para ${listeners.size} listeners")

        /* Notifica todos os listeners registrados */
        for (listener in listeners)
            notifyState(listener)
    }

    /**
     * Notifica o estado da rede, chamando alguma das funções da interface com
     * base no estado atual da rede.
     *
     * @param listener Objeto que implemente a interface [NetworkStateReceiverListener].
     */
    private fun notifyState(listener: NetworkStateReceiverListener) {
        /* Se a variável for nula, para a execução do método */
        if (connected == null) return

        /* Verifica se a rede está conectada ou não */
        if (connected == true)
            /* Chama a função da interface indicando que há uma rede disponível */
            listener.onNetworkAvailable()
        else
            /* Chama a função da interface indicando que não há uma rede disponível */
            listener.onNetworkUnavailable()
    }

    /**
     * Interface interna que gerencia mudanças de estado na conexão para classes que
     * registrarem esse receptor ([NetworkStateReceiver]).
     * Essa interface implementa o 'Strategy Pattern'.
     */
    interface NetworkStateReceiverListener {
        /**
         * Esse método é chamado quando há uma alteração de rede e há uma conexão.
         */
        fun onNetworkAvailable()

        /**
         * Esse método é chamado quando há uma alteração de rede e não há uma conexão.
         */
        fun onNetworkUnavailable()
    }

    companion object {
        private val TAG = NetworkStateReceiver::class.java.simpleName
    }
}