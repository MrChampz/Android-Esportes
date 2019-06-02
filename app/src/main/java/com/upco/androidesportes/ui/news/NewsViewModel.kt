package com.upco.androidesportes.ui.news

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.upco.androidesportes.data.NewsRepository
import com.upco.androidesportes.model.News
import com.upco.androidesportes.model.NewsFetchResult
import com.upco.androidesportes.util.NetworkUtils
import com.upco.androidesportes.util.PreferenceUtils

/**
 * [ViewModel] para a [NewsActivity].
 */
class NewsViewModel(private val repository: NewsRepository): ViewModel() {

    private val newsTrigger = MutableLiveData<Unit>()
    private val newsResult: LiveData<NewsFetchResult> = Transformations.map(newsTrigger) {
        repository.fetchNews()
    }

    val news: LiveData<PagedList<News>> = Transformations.switchMap(newsResult) { it.data }
    val networkErrors: LiveData<String> = Transformations.switchMap(newsResult) { it.networkErrors }

    /**
     * Faz a requisição inicial, os dados em cache serão atualizados de acordo com a API.
     * Esse método é necessário, pois é por meio dele que o LivePagedList,
     * que alimenta o RecyclerView, será criado.
     * Esse método deve ser chamado sempre que o app for aberto.
     *
     * @param context Contexto que será usado para acessar os serviços do sistema.
     */
    fun fetchNews(context: Context) {
        /*
         * Faz a requisição inicial, os dados retornados serão aqueles em cache.
         * Um Unit é "postado" no newsTrigger, fazendo com que o método fetchNews
         * do repositório seja chamado e seu retorno armazenado em newsResult, que
         * por sua vez, faz com que os valores em news e networkErrors sejam atualizados.
         *
         * É necessário chamar o método fetchNews do repositório, pois é por meio dele que
         * o LivePagedList, que alimenta o RecyclerView, será criado.
         */
        newsTrigger.postValue(Unit)

        /* Faz com que os dados em cache sejam atualizados para os mais recentes na API. */
        refreshNews(context)
    }

    /**
     * Atualiza as notícias em cache, com as mais recentes na API, se houver internet.
     *
     * @param context Contexto que será usado para acessar os serviços do sistema.
     */
    fun refreshNews(context: Context) {
        /*
         * Obtém o valor da configuração que indica se os dados devem ser
         * baixados apenas por WiFi ou por redes móveis também.
         * O valor de retorno true indica que ambas as redes podem ser usadas.
         */
        val useBothNetworks = PreferenceUtils.shouldUseBothNetworks(context)

        /* Verifica se o WiFi está conectado */
        val isWifiConnected = NetworkUtils.isWifiConnected(context)

        /* Verifica se a rede móvel está conectada */
        val isMobileConnected = NetworkUtils.isMobileConnected(context)

        /* Indica se os dados locais devem ser atualizados de acordo com a API */
        val fetchNewData = if (isWifiConnected) {
            Log.d(TAG, "O dados serão baixados por WiFi.")
            true
        } else if (useBothNetworks && isMobileConnected) {
            Log.d(TAG, "O dados serão baixados por rede móvel.")
            true
        } else if (!useBothNetworks && isMobileConnected) {
            Log.d(TAG, "Há rede móvel, porém os dados só podem ser baixados por WiFi. " +
                    "Os dados não serão baixados.")
            false
        } else {
            Log.d(TAG, "Não há qualquer tipo de rede. Os dados não serão baixados.")
            false
        }

        /* Atualiza as notícias de acordo com a API */
        if (fetchNewData) {
            repository.refreshNews()
        }
    }

    companion object {
        private val TAG = NewsViewModel::class.java.simpleName
    }
}