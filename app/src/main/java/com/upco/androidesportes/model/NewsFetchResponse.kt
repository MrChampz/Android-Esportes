package com.upco.androidesportes.model

/**
 * Classe de domínio que armazena a resposta da API.
 */
data class NewsFetchResponse(
    val items: List<News> = emptyList()
)