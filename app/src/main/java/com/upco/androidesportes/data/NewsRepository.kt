package com.upco.androidesportes.data

import android.util.Log
import androidx.paging.LivePagedListBuilder
import com.upco.androidesportes.data.database.LocalCache
import com.upco.androidesportes.data.network.Service
import com.upco.androidesportes.model.NewsFetchResult

/**
 * Repositório para notícias [News].
 * Funciona com fonte de dados local (Room) e externa (API).
 *
 * @param service Interface com a API, será utilizada para as requisições.
 * @param cache   Cache por meio do qual os dados serão armazenados localmente.
 */
class NewsRepository(service: Service, cache: LocalCache) {

    /* Obtém o data source factory através do cache */
    private val dataSourceFactory = cache.news()

    /* Constrói o boundary callback */
    private val boundaryCallback = NewsBoundaryCallback(service, cache)

    /**
     * Cria o LiveData com as notícias iniciais (obtidas do cache), o estado das
     * requisições e o estado de atualização.
     *
     * @return [NewsFetchResult] contendo as notícias, o estado das requisições
     * e o estado de atualização.
     */
    fun fetch(): NewsFetchResult {
        /* Loga para fins de debug */
        Log.d(TAG, "Requisitando notícias")

        /* Obtém o PagedList com as notícias */
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()

        /* Obtém os dados expostos pelo boundary callback */
        val networkState = boundaryCallback.networkState
        val refreshState = boundaryCallback.refreshState

        /*
         * Retorna o NewsFetchResult com os dados do PagedList, o estado das
         * requisições e o estado de atualização.
         */
        return NewsFetchResult(data, networkState, refreshState)
    }

    /**
     * Tenta fazer a última requisição novamente.
     * Em caso de erro, quando fazendo download dos dados, esse método deve ser
     * chamado para tentar a requisição novamente.
     */
    fun retry() {
        /* Loga para fins de debug */
        Log.d(TAG, "Tentando requisição novamente")

        /* Faz com que o BoundaryCallback tente fazer a requisição novamente */
        boundaryCallback.retry()
    }

    /**
     * Atualiza as notícias. Limpa os dados locais e faz uma nova requisição à API.
     */
    fun refresh() {
        /* Loga para fins de debug */
        Log.d(TAG, "Atualizando notícias")

        /* Força o Boundary Callback a atualizar as notícias */
        boundaryCallback.refresh()
    }

    companion object {
        private val TAG = NewsRepository::class.java.simpleName

        /* Quantidade de notícias, por página, que serão carregadas do banco de dados */
        private const val DATABASE_PAGE_SIZE = 10
    }
}