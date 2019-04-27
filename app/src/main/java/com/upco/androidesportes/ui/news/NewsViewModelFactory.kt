package com.upco.androidesportes.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Classe Factory que nos permite criar um NewsViewModel.
 */
class NewsViewModelFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel() as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}