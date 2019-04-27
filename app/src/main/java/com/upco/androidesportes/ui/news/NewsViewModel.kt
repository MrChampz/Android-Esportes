package com.upco.androidesportes.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.upco.androidesportes.model.News
import com.upco.androidesportes.model.NewsFetchResult

/**
 * [ViewModel] para a [NewsActivity].
 */
class NewsViewModel(): ViewModel() {

    //private val newsResult: LiveData<NewsFetchResult> = repository.fetchNews()
    private val newsResult = MutableLiveData<NewsFetchResult>()

    val news: LiveData<PagedList<News>> = Transformations.switchMap(newsResult) { it.data }
    val networkErrors: LiveData<String> = Transformations.switchMap(newsResult) { it.networkErrors }
}