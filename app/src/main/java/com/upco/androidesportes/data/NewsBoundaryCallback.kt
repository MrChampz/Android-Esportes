package com.upco.androidesportes.data

import android.util.Log
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
     * Esse método força uma atualização das notícias. Os dados em cache serão
     * deletados e os novos dados, obtidos a partir da API, serão inseridos.
     */
    fun refresh() {
        /*
         * Faz a requisição à API e, se houver sucesso,
         * limpa o cache e salva os novos dados.
         */
        requestAndSaveData(true)
    }

    /**
     * Faz a requisição à API. Se houver sucesso, as notícias retornadas
     * serão inseridas no banco de dados [cache]. Caso contrário, o erro
     * será "postado" no LiveData [_networkErrors] para que seja exibido
     * ao usuário.
     *
     * @param clearCache Indica se o cache deve ser resetado ou não..
     *                   No caso de uma atualização, ele deve ser resetado.
     */
    private fun requestAndSaveData(clearCache: Boolean = false) {
        /* Se já houver uma requisição sendo feita, termina a execução do método aqui */
        if (isRequestInProgress) return

        /*
         * Se for para limpar o cache, reseta o índice atual. Isso é necessário,
         * pois os dados serão baixados novamente partindo da página 1.
         */
        if (clearCache) lastRequestedPage = 1

        /*
         * Faz a requisição à API, usando a função estática [fetchNews].
         * Verifica se essa é uma requisição comum, em que os dados são agregados,
         * ou se é uma requisição de atualização, nesse caso o cache será limpo e
         * todos os dados serão baixados novamente.
         * Em caso de erro, esse será "postado" no LiveData e, consequentemente,
         * repassado às outras camadas da aplicação.
         */
        isRequestInProgress = true
        fetchNews(service, lastRequestedPage, { news ->
            if (clearCache) {
                cache.clear {
                    cache.insert(news) {
                        lastRequestedPage++
                        isRequestInProgress = false
                    }
                }
            } else {
                cache.insert(news) {
                    lastRequestedPage++
                    isRequestInProgress = false
                }
            }
        }, { error ->
            _networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }
}