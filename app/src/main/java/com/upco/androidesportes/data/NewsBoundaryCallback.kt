package com.upco.androidesportes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.upco.androidesportes.data.database.LocalCache
import com.upco.androidesportes.data.network.Service
import com.upco.androidesportes.data.network.fetchNews
import com.upco.androidesportes.model.NetworkState
import com.upco.androidesportes.model.News
import com.upco.androidesportes.util.PagingRequestHelper
import java.util.concurrent.Executors

/**
 * [PagedList.BoundaryCallback] para a manipulação de notícias ([News]).
 * Faz o gerenciamento dos dados recebidos através da API, do estado da
 * requisição atual, do estado de atualização, caso esteja acontecendo alguma,
 * e dos erros, caso haja.
 *
 * Faz a requisição de mais notícias sempre que a última notícia no [PagedList]
 * for alcançada.
 *
 * Fornece métodos para atualizar o [PagedList] inteiro, assim dados recentes da
 * API serão obtidos, e para tentar executar a última requisição novamente.
 *
 * @param service Interface com a API, será utilizada para as requisições.
 * @param cache   Cache por meio do qual os dados serão armazenados localmente.
 */
class NewsBoundaryCallback(
    private val service: Service,
    private val cache: LocalCache
): PagedList.BoundaryCallback<News>() {

    /* LiveData com o estado atual da requisição */
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _networkState = MutableLiveData<NetworkState>()

    /* LiveData com o estado atual da atualização, se estiver ocorrendo */
    val refreshState: LiveData<NetworkState>
        get() = _refreshState

    private val _refreshState = MutableLiveData<NetworkState>()

    /*
     * Armazena o índice da última página requisitada.
     * Quando a requisição for sucedida, incrementa o índice.
     */
    private var lastRequestedPage = 1

    /* Evita fazer múltiplas requisições ao mesmo tempo */
    private var isRequestInProgress = false

    /*
     * Classe auxiliar que gerencia o estado das requisições, assim garantimos que
     * requisições não serão executadas caso já haja alguma em andamento. Também é
     * por meio dela que podemos tentar novamente a última requisição.
     */
    private val helper = PagingRequestHelper(Executors.newSingleThreadExecutor())

    /**
     * Método chamado quando não é retornado nada, no carregamento
     * inicial da fonte de dados.
     */
    override fun onZeroItemsLoaded() {
        /*
         * Faz a requisição à API e, se houver sucesso,
         * salva os dados no banco de dados.
         */
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            requestAndSaveData(it)
        }
    }

    /**
     * Método chamado quando a notícia no final do [PagedList] for carregada.
     * @param itemAtEnd Notícia no final do [PagedList].
     */
    override fun onItemAtEndLoaded(itemAtEnd: News) {
        /*
         * Faz a requisição à API e, se houver sucesso,
         * salva os dados no banco de dados.
         */
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            requestAndSaveData(it)
        }
    }

    /**
     * Tenta executar a última requisição novamente.
     */
    fun retry() = helper.retryAllFailed()

    /**
     * Esse método força uma atualização das notícias. Os dados em cache serão
     * deletados e os novos dados, obtidos a partir da API, serão inseridos.
     */
    fun refresh() {
        /*
         * Faz a requisição à API e, se houver sucesso,
         * limpa o cache e salva os novos dados.
         */
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            requestAndSaveData(it, true)
        }
    }

    /**
     * Faz a requisição à API. Se houver sucesso, as notícias retornadas serão
     * inseridas no banco de dados, por meio do [cache]. Caso contrário, o erro
     * será "postado" juntamente com o estado da requisição no [LiveData]
     * [_networkState], em caso de uma requisição comum, e no [LiveData]
     * [_refreshState], em caso de uma requisição de atualização, para que seja
     * exibido ao usuário.
     *
     * @param callback Callback que será utilizado para indicar ao [helper]
     *                 o estado da requisição.
     * @param refresh  Indica se é uma atualização dos dados, e nesse caso o
     *                 cache deve ser resetado e preenchido com dados "frescos"
     *                 vindos da API.
     */
    private fun requestAndSaveData(
        callback: PagingRequestHelper.Request.Callback,
        refresh: Boolean = false
    ) {
        /* Se já houver uma requisição sendo feita, termina a execução do método aqui */
        if (isRequestInProgress) return

        if (refresh)
        {
            /*
             * Se for para limpar o cache, reseta o índice atual. Isso é necessário,
             * pois os dados serão baixados novamente partindo da página 1.
             */
            lastRequestedPage = 1

            /* Atualiza o estado, indicando que há uma atualização em progresso */
            _refreshState.postValue(NetworkState.LOADING)
        } else {
            /* Atualiza o estado, indicando que há uma requisição em progresso */
            _networkState.postValue(NetworkState.LOADING)
        }

        /*
         * Faz a requisição à API, usando a função estática [fetch].
         * Verifica se essa é uma requisição comum, em que os dados são agregados,
         * ou se é uma requisição de atualização, nesse caso o cache será limpo e
         * todos os dados serão baixados novamente.
         * Em caso de erro, esse será "postado" no LiveData e, consequentemente,
         * repassado às outras camadas da aplicação.
         */
        isRequestInProgress = true
        fetchNews(service, lastRequestedPage, { news ->
            if (refresh) {
                cache.clear {
                    cache.insert(news) {
                        lastRequestedPage++
                        isRequestInProgress = false

                        /* Atualiza o estado, indicando que houve sucesso na atualização */
                        _refreshState.postValue(NetworkState.LOADED)

                        /* Indica ao helper que houve sucesso na atualização */
                        callback.recordSuccess()
                    }
                }
            } else {
                cache.insert(news) {
                    lastRequestedPage++
                    isRequestInProgress = false

                    /* Atualiza o estado, indicando que houve sucesso na requisição */
                    _networkState.postValue(NetworkState.LOADED)

                    /* Indica ao helper que houve sucesso na requisição */
                    callback.recordSuccess()
                }
            }
        }, { error ->
            isRequestInProgress = false

            /* Atualiza o estado, indicando que houve algum erro na requisição/atualização */
            if (refresh) _refreshState.value = NetworkState.error(error)
            else _networkState.postValue(NetworkState.error(error))

            /* Indica ao helper que houve algum erro na requisição/atualização */
            callback.recordFailure(Throwable(error))
        })
    }
}