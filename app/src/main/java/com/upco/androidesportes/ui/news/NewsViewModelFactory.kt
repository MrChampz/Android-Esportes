package com.upco.androidesportes.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.upco.androidesportes.data.NewsRepository

/**
 * Classe Factory que nos permite criar um [NewsViewModel].
 *
 * @param repository Repositório que será utilizado na obtenção das notícias.
 */
class NewsViewModelFactory(private val repository: NewsRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}