package com.upco.androidesportes.data

import android.util.Log
import androidx.paging.LivePagedListBuilder
import com.upco.androidesportes.data.database.LocalCache
import com.upco.androidesportes.data.network.Service
import com.upco.androidesportes.data.network.fetchNews
import com.upco.androidesportes.model.NewsFetchResult

/**
 * Repositório para notícias [News].
 * Funciona com fonte de dados local (Room) e externa (API).
 */
class NewsRepository(service: Service, cache: LocalCache) {

    /* Obtém o data source factory através do cache */
    private val dataSourceFactory = cache.news()

    /* Constrói o boundary callback */
    private val boundaryCallback = NewsBoundaryCallback(service, cache)

    /**
     * Faz uma requisição por notícias, que poderão ter como fonte de dados
     * o banco de dados ou a API.
     *
     * @return [NewsFetchResult] contendo os as notícias e os erros, se houver.
     */
    fun fetchNews(): NewsFetchResult {
        Log.d(TAG, "Requisitando notícias")

        /* Obtém o [PagedList] com as notícias */
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()

        /* Obtém os erros de rede expostos pelo boundary callback */
        val networkErrors = boundaryCallback.networkErrors

        /* Retorna o [NewsFetchResult] com os dados do [PagedList] e os erros, se houver */
        return NewsFetchResult(data, networkErrors)
    }

    /**
     * Atualiza as notícias. Limpa os dados locais e faz uma nova requisição à API.
     */
    fun refreshNews() {
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