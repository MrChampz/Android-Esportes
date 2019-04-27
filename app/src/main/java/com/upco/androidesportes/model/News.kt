package com.upco.androidesportes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Classe de modelo que armazena todas as informações sobre uma notícia.
 * Esse classe também define a tabela "news" no Room, onde o [uid] da notícia é a chave primária.
 */
@Entity(tableName = "news")
data class News(
        @PrimaryKey val uid: Long,
        val chapeu: String,
        val title: String,
        val summary: String,
        val timestamp: String,
        val image: String,
        val url: String
)