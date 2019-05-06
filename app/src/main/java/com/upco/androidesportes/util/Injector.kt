package com.upco.androidesportes.util

import android.content.Context
import com.upco.androidesportes.data.NewsRepository
import com.upco.androidesportes.data.database.LocalCache
import com.upco.androidesportes.data.database.NewsDatabase
import com.upco.androidesportes.data.network.Service
import com.upco.androidesportes.ui.news.NewsViewModelFactory
import java.util.concurrent.Executors

/**
 * Fornece métodos estáticos para injetar as várias classes necessárias.
 * Dessa forma, objetos podem ser passados como parâmetros nos
 * construtores e então substituídos para testes, aonde for necessário.
 */
object Injector {

    /**
     * Cria uma instância de [LocalCache] baseado no DAO do banco de dados.
     */
    private fun provideCache(context: Context): LocalCache {
        val database = NewsDatabase.getInstance(context)
        return LocalCache(database.newsDao(), Executors.newSingleThreadExecutor())
    }

    /**
     * Cria uma instância de [NewsRepository] baseado no [Service] e [LocalCache].
     */
    private fun provideNewsRepository(context: Context): NewsRepository {
        return NewsRepository(Service.create(), provideCache(context))
    }

    /**
     * Fornece o [NewsViewModelFactory] que é usado para obter
     * referencias para objetos [NewsViewModel].
     */
    fun provideNewsViewModelFactory(context: Context): NewsViewModelFactory {
        return NewsViewModelFactory(provideNewsRepository(context))
    }
}