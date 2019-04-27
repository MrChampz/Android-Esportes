package com.upco.androidesportes.util

import android.content.Context
import com.upco.androidesportes.ui.news.NewsViewModelFactory

/**
 * Fornece métodos estáticos para injetar as várias classes necessárias.
 * Dessa forma, objetos podem ser passados como parâmetros nos
 * construtores e então substituídos para testes, aonde for necessário.
 */
object Injector {

    /**
     * Fornece o [NewsViewModelFactory] que é usado para obter
     * referencias para objetos [NewsViewModel].
     */
    fun provideNewsViewModelFactory(context: Context): NewsViewModelFactory {
        return NewsViewModelFactory()
    }
}