package com.upco.androidesportes.ui.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.upco.androidesportes.data.NewsRepository

/**
 * [ViewModel] para a [NewsActivity].
 *
 * @param repository Repositório que será utilizado na obtenção das notícias.
 */
class NewsViewModel(private val repository: NewsRepository): ViewModel() {

    /* Usado como gatilho para obtenção das notícias a partir do repository */
    private val newsTrigger = MutableLiveData<Unit>()

    /* Resultado da requisição ao repository */
    private val newsResult = map(newsTrigger) { repository.fetch() }

    /** LiveData com as notícias. */
    val news = switchMap(newsResult) { it.data }

    /** LiveData com o estado da requisição atual. */
    val networkState = switchMap(newsResult) { it.networkState }

    /** LiveData com o estado da atualização, caso esteja ocorrendo alguma. */
    val refreshState = switchMap(newsResult) { it.refreshState }

    /**
     * Faz a requisição inicial, as noticías retornadas serão aquelas em cache.
     * Esse método é necessário, pois é por meio dele que o PagedList, que
     * alimenta o RecyclerView, será criado.
     * Esse método deve ser chamado sempre que o app for aberto.
     */
    fun fetch() {
        /*
         * Faz a requisição inicial, os dados retornados serão aqueles em cache.
         * Um valor nulo é "postado" no newsTrigger, fazendo com que o método
         * fetch do repositório seja chamado e seu retorno armazenado em
         * newsResult, que por sua vez, faz com que os valores em news,
         * networkState e refreshState sejam atualizados.
         *
         * É necessário chamar o método fetch do repositório, pois é por meio
         * dele que o PagedList, que alimenta o RecyclerView, será criado.
         */
        newsTrigger.value = null
    }

    /**
     * Tenta executar a última requisição novamente.
     */
    fun retry() = repository.retry()

    /**
     * Atualiza as notícias em cache, com as mais recentes na API.
     */
    fun refresh() = repository.refresh()
}