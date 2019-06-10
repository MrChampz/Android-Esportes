package com.upco.androidesportes.data.network

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.upco.androidesportes.model.News
import com.upco.androidesportes.model.NewsFetchResponse
import com.upco.androidesportes.util.DateUtils
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.*

class Deserializer: JsonDeserializer<NewsFetchResponse> {

    /**
     * Deserializa a resposta da API, e retorna um [NewsFetchResponse] com os dados.
     *
     * @param json O Json sendo deserializado
     * @param typeOfT O tipo do Objeto para o qual o Json será deserializado
     * @param context Contexto para a deserialização
     * @return [NewsFetchResponse] com os dados obtidos da API.
     * @throws JsonParseException se o [json] não estiver no formato esperado por [typeOfT].
     */
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): NewsFetchResponse {
        val items = mutableListOf<News>()

        /* Percorre cada item no array "items" do Json */
        for (item in json!!.asJsonObject["items"].asJsonArray) {

            /* Campo "id" do item */
            val id = item.asJsonObject["id"].asString

            /* Campo "type" do item */
            val type = item.asJsonObject["type"].asString

            /*
             * Verifica se o id da notícia existe e se o tipo da notícia é "basico",
             * já que nós só queremos exibir notícias desse tipo.
             */
            if (id != null && type == "basico") {
                /* Objeto "content" do item */
                val content = item.asJsonObject["content"].asJsonObject

                /*
                 * Verifica se o objeto "image" existe, e se sim, pega o campo "url".
                 */
                var image: String? = null
                if (content["image"] != null) {
                    image = content["image"].asJsonObject["url"].asString

                    /* Se houver caracteres de quebra de linha, remove-os */
                    image = image.replace("\n", "")
                }

                /*
                 * Verifica se o objeto "chapeu" existe, e se sim, pega o campo "label".
                 */
                var chapeu: String? = null
                if (content["chapeu"] != null) {
                    chapeu = content["chapeu"].asJsonObject["label"].asString

                    /* Se houver caracteres de quebra de linha, remove-os */
                    chapeu = chapeu.replace("\n", "")
                }

                /*
                 * Verifica se o campo "title" existe, e se sim, pega seu valor.
                 */
                var title: String? = null
                if (content["title"] != null) {
                    title = content["title"].asString

                    /* Se houver caracteres de quebra de linha, remove-os */
                    title = title.replace("\n", "")
                }

                /*
                 * Verifica se o campo "summary" existe, e se sim, pega seu valor.
                 */
                var summary: String? = null
                if (content["summary"] != null) {
                    summary = content["summary"].asString

                    /* Se houver caracteres de quebra de linha, remove-os */
                    summary = summary.replace("\n", "")
                }

                /*
                 * Verifica se o campo "url" existe, e se sim, pega seu valor.
                 */
                var url: String? = null
                if (content["url"] != null) {
                    url = content["url"].asString

                    /* Se houver caracteres de quebra de linha, remove-os */
                    url = url.replace("\n", "")
                }

                /*
                 * Verifica se o campo "publication" existe no item, e se sim, pega seu valor.
                 * Converte o timestamp do formato vindo da API em um formato usado no Android.
                 */
                var publication: Long? = null
                if (item.asJsonObject["publication"] != null) {
                    var timestamp = item.asJsonObject["publication"].asString

                    /*
                     * Remove os últimos 7 caracteres do timestamp, sendo 6 caracteres
                     * dos milissegundos e 1 do time zone ('Z'). Por fim, adiciona o
                     * 'Z' novamente.
                     * Isso é necessário, pois a API de data/hora do Java (antes da
                     * versão 8) não é tão precisa, e também não haverá grande impacto
                     * no resultado final já que se trata de milissegundos.
                     */
                    timestamp = timestamp.substring(0, timestamp.length - 7) + 'Z'

                    /* Converte o timestamp em milissegundos, formato salvo no banco de dados */
                    publication = DateUtils.getUtcFromTimestamp(timestamp)
                }

                /*
                 * Com todos os campos deserializados, cria-se um [News] com os dados
                 * e então adiciona-o à lista.
                 */
                val news = News(id, image, chapeu, title, summary, url, publication)
                items.add(news)
            }
        }

        /* Retorna um [NewsFetchResponse], com as notícias [items] obtidas da API */
        return NewsFetchResponse(items)
    }
}