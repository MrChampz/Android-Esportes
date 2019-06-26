package com.upco.androidesportes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.upco.androidesportes.model.News

/**
 * Esquema de banco de dados que armazena uma lista de notícias.
 */
@Database(entities = [News::class], version = 1, exportSchema = false)
abstract class NewsDatabase: RoomDatabase() {

    /**
     * DAO para acessar a tabela de [News] no Room.
     */
    abstract fun newsDao(): NewsDao

    companion object {
        /* Nome do arquivo do banco de dados */
        private const val DATABASE_NAME = "AndroidEsportes_News.db"

        /* Instância singleton do banco de dados */
        @Volatile private var instance: NewsDatabase? = null

        /**
         * Retorna a instância singleton da [NewsDatabase].
         * Na primeira instanciação da classe [NewsDatabase] esse método será chamado e
         * criará o banco de dados. Nas próximas instanciações a instância já criada será retornada.
         *
         * @param context Contexto Android.
         * @return Instância singleton da [NewsDatabase].
         */
        fun getInstance(context: Context): NewsDatabase =
                instance ?: synchronized(this) {
                    instance ?: buildDatabase(context).also { instance = it }
                }

        /**
         * Cria o banco de dados.
         *
         * @param context Contexto Android.
         * @return Instância de [NewsDatabase].
         */
        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    DATABASE_NAME
                ).build()
    }
}