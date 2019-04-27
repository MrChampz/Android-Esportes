package com.upco.androidesportes.model

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

/**
 * Armazena o resultado da requisição à API, contendo um LiveData<List<News>> com os dados
 * retornados, e um LiveData<String> com erros de requisição/rede.
 */
data class NewsFetchResult(
    val data: LiveData<PagedList<News>>,
    val networkErrors: LiveData<String>
)