package com.upco.androidesportes.util

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.upco.androidesportes.R

/**
 * Fornece métodos auxiliares para o gerenciamento das preferencias.
 */
object PreferenceUtils {

    private val TAG = PreferenceUtils::class.java.simpleName

    /**
     * Verifica de acordo com as configurações se os dados devem ser baixados por
     * ambas as redes ou apenas por WiFi. Se o retorno for true, ambas as redes
     * podem ser usadas.
     *
     * @param context Contexto que será usado para acessar os serviços do sistema.
     * @return Retorna true caso ambas as redes possam ser usadas, caso contrário,
     *         retorna false.
     */
    fun shouldUseBothNetworks(context: Context): Boolean {
        /*
         * Obtém o PreferenceManager padrão, que nos possibilitará pegar as
         * configurações setadas.
         */
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        /*
         * Obtém o valor da configuração que indica se os dados devem ser
         * baixados apenas por WiFi ou por redes móveis também.
         * O valor de retorno true indica que ambas as redes podem ser usadas.
         */
        val useBothNetworks = prefs.getBoolean(
            context.resources.getString(R.string.tx_data_download_key),
            false
        )

        /* Loga para fins de debug */
        if (useBothNetworks)
            Log.d(TAG, "O dados podem ser baixados por WiFi ou por rede móvel.")
        else
            Log.d(TAG, "O dados podem ser baixados apenas por WiFi.")

        /* Retorna o valor definido nas configurações */
        return useBothNetworks
    }
}