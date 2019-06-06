package com.upco.androidesportes.ui.news

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.google.android.material.snackbar.Snackbar
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
     * Faz a requisição inicial, as noticías retornadas serão aquelas em cache.
     * Esse método é necessário, pois é por meio dele que o LivePagedList,
     * que alimenta o RecyclerView, será criado.
     * Esse método deve ser chamado sempre que o app for aberto.
     */
    fun fetchNews() {
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
    }

    /**
     * Atualiza as notícias em cache, com as mais recentes na API.
     */
    fun refreshNews() = repository.refreshNews()
}