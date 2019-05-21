package com.upco.androidesportes.data.database

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.upco.androidesportes.model.News


/**
 * DAO para acessar a tabela de [News] no Room.
 */
@Dao
interface NewsDao {

    /**
     * Insere uma lista de notícias no banco de dados.
     * No caso de conflito com dados já existentes, esses serão substituídos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(news: List<News>)

    /**
     * Faz uma requisição similar à da API:
     * Obtém as notícias armazenadas no banco de dados,
     * e as retorna em ordem descendente, da mais recente para a mais antiga.
     */
    @Query("SELECT * FROM news ORDER BY publication DESC")
    fun news(): DataSource.Factory<Int, News>
}