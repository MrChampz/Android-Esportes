package com.upco.androidesportes.data.database

import android.util.Log
import com.upco.androidesportes.model.News
import java.util.concurrent.Executor

/**
 * Classe que manipula a fonte de dados local do DAO. Desta forma é garantido que os
 * métodos serão executados no [Executor] (thread) correto.
 */
class LocalCache(private val newsDao: NewsDao, private val ioExecutor: Executor) {

    companion object {
        private val TAG = LocalCache::class.java.simpleName
    }

    /**
     * Insere uma lista de notícias no banco de dados, em uma thread, em segundo plano.
     *
     * @param news Lista de notícias.
     * @param insertFinished Função que será executada ao final da inserção.
     */
    fun insert(news: List<News>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            Log.d(TAG, "Inserindo ${news.size} notícias")
            newsDao.insert(news)
            insertFinished()
        }
    }

    /**
     * Requere um LiveData<List<News>>, com a lista de notícias presentes
     * no banco da dados, ao DAO.
     */
    fun news() = newsDao.news()
}