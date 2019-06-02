package com.upco.androidesportes.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

/**
 * Fornece métodos auxiliares para o gerenciamento de redes.
 */
object NetworkUtils {

    private val TAG = NetworkUtils::class.java.simpleName

    /**
     * Verifica se há uma conexão WiFi ativa.
     *
     * @param context Contexto que será usado para acessar os serviços do sistema.
     * @return Retorna true caso haja, caso contrário, retorna false.
     */
    fun isWifiConnected(context: Context): Boolean {
        /* Verifica se há alguma rede ativa e conectada */
        val isConnected = isConnected(context)

        if (isConnected) {
            /* Verifica se o tipo da rede é WiFi */
            val isWifi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                /*
                 * A partir da API 23 (M) essa verificação deve ser feita por
                 * meio da classe NetworkCapabilities.
                 */
                val cap = getActiveNetworkCapabilities(context)
                cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            } else {
                /* De modo a suportar as versões anteriores à 23, usamos a API antiga */
                val net = getActiveNetworkInfo(context)
                net?.type == TYPE_WIFI
            }

            /* Se for uma rede WiFi, retorna true */
            if (isWifi) {
                /* Loga para fins de debug */
                Log.d(TAG, "A rede WiFi está conectada")

                /* Retorna true, indicando que é uma rede WiFi */
                return true
            }
        }

        /* O valor de retorno padrão é false */
        return false
    }

    /**
     * Verifica se há uma conexão móvel ativa.
     *
     * @param context Contexto que será usado para acessar os serviços do sistema.
     * @return Retorna true caso haja, caso contrário, retorna false.
     */
    fun isMobileConnected(context: Context): Boolean {
        /* Verifica se há alguma rede ativa e conectada */
        val isConnected = isConnected(context)

        if (isConnected) {
            /* Verifica se o tipo da rede é móvel */
            val isMobile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                /*
                 * A partir da API 23 (M) essa verificação deve ser feita por
                 * meio da classe NetworkCapabilities.
                 */
                val cap = getActiveNetworkCapabilities(context)
                cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            } else {
                /* De modo a suportar as versões anteriores à 23, usamos a API antiga */
                val net = getActiveNetworkInfo(context)
                net?.type == TYPE_MOBILE
            }

            /* Se for uma rede móvel, retorna true */
            if (isMobile) {
                /* Loga para fins de debug */
                Log.d(TAG, "A rede móvel está conectada")

                /* Retorna true, indicando que é uma rede móvel */
                return true
            }
        }

        /* O valor de retorno padrão é false */
        return false
    }

    /**
     * Retorna se há ou não uma conexão ativa (WiFi, Móvel, Bluetooth, etc).
     *
     * @param context Contexto que será usado para acessar os serviços do sistema.
     * @return Retorna true caso haja, caso contrário, retorna false.
     */
    fun isConnected(context: Context): Boolean {
        /* Obtém as informações da rede ativa (se houver) */
        val net = getActiveNetworkInfo(context)

        /* Retorna se ela está conectada ou não */
        return net?.isConnected ?: false
    }

    /**
     * Retorna um [NetworkInfo] com informações da rede ativa. Se não houver
     * nenhuma rede ativa, retorna null.
     *
     * @param context Contexto que será usado para acessar os serviços do sistema.
     * @return Um [NetworkInfo] com informações da rede ativa. Se não houver rede ativa, null.
     */
    private fun getActiveNetworkInfo(context: Context): NetworkInfo? {
        /* Obtém o ConnectivityManager, que nos possibilita acessar informações da rede */
        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        /* Retorna informações da rede ativa, ou null caso não haja uma */
        return cm.activeNetworkInfo
    }

    /**
     * Retorna um [NetworkCapabilities] com as capacidades da rede ativa.
     * Com o [NetworkCapabilities] podemos verificar se essa rede é WiFi ou móvel.
     *
     * @param context Contexto que será usado para acessar os serviços do sistema.
     * @return Um [NetworkCapabilities] com as capacidades da rede ativa.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getActiveNetworkCapabilities(context: Context): NetworkCapabilities {
        /* Obtém o ConnectivityManager, que nos possibilita acessar informações da rede */
        val cm = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        /* Retorna as capacidades da rede ativa */
        return cm.getNetworkCapabilities(cm.activeNetwork)
    }
}