package com.upco.androidesportes.data

import android.util.Log
import androidx.paging.LivePagedListBuilder
import com.upco.androidesportes.data.database.LocalCache
import com.upco.androidesportes.data.network.Service
import com.upco.androidesportes.model.NewsFetchResult

/**
 * Repositório para notícias [News].
 * Funciona com fonte de dados local (Room) e externa (API).
 */
class NewsRepository(private val service: Service, private val cache: LocalCache) {

    /**
     * Faz uma requisição por notícias, que poderão ter como fonte de dados
     * o banco de dados ou a API.
     *
     * @return [NewsFetchResult] contendo os as notícias e os erros, se houver.
     */
    fun fetchNews(): NewsFetchResult {
        Log.d(TAG, "Requisitando notícias")

        /* Obtém o data source factory através do cache */
        val dataSourceFactory = cache.news()

        /* Constrói o boundary callback */
        val boundaryCallback = NewsBoundaryCallback(service, cache)

        /* Obtém os erros de rede expostos pelo boundary callback */
        val networkErrors = boundaryCallback.networkErrors

        /* Obtém o [PagedList] com as notícias */
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()

        /* Retorna o [NewsFetchResult] com os dados do [PagedList] e os erros, se houver */
        return NewsFetchResult(data, networkErrors)
    }

    companion object {
        private val TAG = NewsRepository::class.java.simpleName

        /* Quantidade de notícias, por página, que serão carregadas do banco de dados */
        private const val DATABASE_PAGE_SIZE = 10
    }
}