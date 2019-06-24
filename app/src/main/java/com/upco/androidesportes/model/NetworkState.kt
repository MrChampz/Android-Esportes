package com.upco.androidesportes.model

/**
 * Indica o status de uma requisição.
 * <p/>
 * RUNNING -> Há uma requisição em andamento.
 * SUCCESS -> A requisição foi completada com sucesso.
 * FAILED  -> Houve algum tipo de erro na requisição
 */
enum class Status { RUNNING, SUCCESS, FAILED }

/**
 * Armazena o status da requisição atual feita à API.
 * Essa classe deve ser usado por meio das instâncias estáticas:
 * [LOADED], [LOADING], ou por meio da função estática [error(String?)][error]
 * <p/>
 * @param status Status da requisição.
 * @param msg    Mensagem de erro, em caso de erro.
 */
@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
        val status: Status,
        val msg: String? = null
) {
    companion object {
        /**
         * Instância estática de [NetworkState] com o [status] de [Status.SUCCESS].
         * Indica que a requisição já foi completada e houve sucesso.
         */
        val LOADED = NetworkState(Status.SUCCESS)

        /**
         * Instância estática de [NetworkState] com o [status] de [Status.RUNNING].
         * Indica que a requisição está em progresso.
         */
        val LOADING = NetworkState(Status.RUNNING)

        /**
         * Função estática que retorna uma instância de [NetworkState] com o status
         * [Status.FAILED] e com a mensagem de erro [msg].
         * Indica que a requisição já foi completada, mas houve algum erro.
         *
         * @param msg Mensagem de erro.
         * @return Uma instância de [NetworkState] com o status [Status.FAILED].
         */
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
    }
}