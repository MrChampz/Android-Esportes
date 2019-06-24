package com.upco.androidesportes.model

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

/**
 * Armazena o resultado da requisição à API, contendo um [LiveData] com os dados
 * retornados, um [NetworkState] com o status da requisição, e um [NetworkState]
 * com o status da requisição feita durante uma atualização, sendo assim, o valor
 * de [refreshState] só é alterado quando uma atualização é requisitada.
 *
 * @param data         O [LiveData] com as listas de notícias para a UI observar.
 * @param networkState Representa o status da requisição atual à API, é atualizado sempre
 *                     que houver uma nova requisição.
 * @param refreshState Representa o status da atualização. Diferente do [networkState],
 *                     esse valor só tem importancia quando uma atualização é requisitada.
 */
data class NewsFetchResult(
        val data: LiveData<PagedList<News>>,
        val networkState: LiveData<NetworkState>,
        val refreshState: LiveData<NetworkState>
)