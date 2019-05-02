package com.upco.androidesportes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Classe de domínio que armazena todas as informações sobre uma notícia.
 * Esse classe também define a tabela "news" no Room, onde o [uid] da notícia é a chave primária.
 *
 * Construtor usado pelo Room para criar instâncias de [News].
 */
@Entity(tableName = "news")
data class News(
        @PrimaryKey(autoGenerate = true)
        val uid: Int?,
        val image: String?,
        val chapeu: String?,
        val title: String?,
        val summary: String?,
        val url: String?,
        val age: Long?
) {
    /**
     * Construtor usado pelo [Deserializer]. Quando a requisição é bem sucedida e retorna os dados,
     * em JSON, ele os converte em instâncias de [News] usando esse construtor.
     *
     * @param image   Url da imagem da notícia.
     * @param chapeu  Campo "chapeu" da notícia.
     * @param title   Título da notícia.
     * @param summary Sumário da notícia.
     * @param url     Url da notícia.
     * @param age     Tempo (em segundos) desde que a notícia foi criada.
     */
    constructor(
        image: String?,
        chapeu: String?,
        title: String?,
        summary: String?,
        url: String?,
        age: Long?
    ): this(null, image, chapeu, title, summary, url, age)
}