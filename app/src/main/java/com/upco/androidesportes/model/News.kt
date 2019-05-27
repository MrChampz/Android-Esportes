package com.upco.androidesportes.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Classe de domínio que armazena todas as informações sobre uma notícia.
 * Esse classe também define a tabela "news" no Room, onde o [id] da notícia é a chave primária.
 *
 * @param id          Id da notícia.
 * @param image       Url da imagem da notícia.
 * @param chapeu      Campo "chapeu" da notícia.
 * @param title       Título da notícia.
 * @param summary     Sumário da notícia.
 * @param url         Url da notícia.
 * @param publication Timestamp (em millis) de quando a notícia foi publicada.
 */
@Entity(tableName = "news")
@Parcelize
data class News(
    @PrimaryKey
    val id: String,
    val image: String?,
    val chapeu: String?,
    val title: String?,
    val summary: String?,
    val url: String?,
    val publication: Long?
): Parcelable {
    companion object {
        /** Usada para passar um [News], como extra, por meio de intents. */
        const val KEY = "news"
    }
}