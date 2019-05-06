package com.upco.androidesportes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.upco.androidesportes.data.database.LocalCache
import com.upco.androidesportes.data.network.Service
import com.upco.androidesportes.data.network.fetchNews
import com.upco.androidesportes.model.News

/**
 * [PagedList.BoundaryCallback] para a manipulação de [News].
 * Faz o gerenciamento dos dados recebidos através da API, e dos erros, caso haja.
 * Faz a requisição de mais notícias sempre que, o [PagedList] vinculado,
 * alcançar o último item disponível.
 */
class NewsBoundaryCallback(
    private val service: Service,
    private val cache: LocalCache
): PagedList.BoundaryCallback<News>() {

    /* LiveData com os erros de rede, caso haja algum */
    val networkErrors: LiveData<String>
        get() = _networkErrors

    private val _networkErrors = MutableLiveData<String>()

    /*
     * Armazena o índice da última página requisitada.
     * Quando a requisição for sucedida, incrementa o índice.
     */
    private var lastRequestedPage = 1

    /* Evita fazer múltiplas requisições ao mesmo tempo */
    private var isRequestInProgress = false

    /**
     * Método chamado quando não é retornado nada, no carregamento
     * inicial da fonte de dados.
     */
    override fun onZeroItemsLoaded() {
        /*
         * Faz a requisição à API e, se houver sucesso,
         * salva os dados no banco de dados.
         */
        requestAndSaveData()
    }

    /**
     * Método chamado quando o item no final da [PagedList] for carregado.
     * @param itemAtEnd Último item da [PagedList].
     */
    override fun onItemAtEndLoaded(itemAtEnd: News) {
        /*
         * Faz a requisição à API e, se houver sucesso,
         * salva os dados no banco de dados.
         */
        requestAndSaveData()
    }

    /**
     * Faz a requisição à API. Se houver sucesso, as notícias retornadas
     * serão inseridas no banco de dados [cache]. Caso contrário, o erro
     * será "postado" no LiveData [_networkErrors], e consequentemente
     * será exibido para o usuário, por meio de um [Toast].
     */
    private fun requestAndSaveData() {
        /* Se já houver uma requisição sendo feita, termina a execução do método aqui */
        if (isRequestInProgress) return

        /*
         * Faz a requisição à API, usando a função estática [fetchNews].
         */
        isRequestInProgress = true
        fetchNews(service, lastRequestedPage, { news ->
            cache.insert(news) {
                lastRequestedPage++
                isRequestInProgress = false
            }
        }, { error ->
            _networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }
}