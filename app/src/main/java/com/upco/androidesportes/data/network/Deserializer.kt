package com.upco.androidesportes.data.network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.upco.androidesportes.model.News
import com.upco.androidesportes.model.NewsFetchResponse
import java.lang.reflect.Type

class Deserializer: JsonDeserializer<NewsFetchResponse> {

    /**
     * Deserializa a resposta da API, e retorna um [NewsFetchResponse] com os dados.
     *
     * @param json O Json sendo deserializado
     * @param typeOfT O tipo do Objeto para o qual o Json será deserializado
     * @param context Contexto para a deserialização
     * @return [NewsFetchResponse] com os dados obtidos da API.
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
            /* Objeto "content" do Json */
            val content = item.asJsonObject["content"].asJsonObject

            /*
             * Verifica se o objeto "image" existe, e se sim, pega o campo "url".
             */
            var image: String? = null
            if (content["image"] != null) {
                image = content["image"].asJsonObject["url"].asString
            }

            /*
             * Verifica se o objeto "chapeu" existe, e se sim, pega o campo "label".
             */
            var chapeu: String? = null
            if (content["chapeu"] != null) {
                chapeu = content["chapeu"].asJsonObject["label"].asString
            }

            /*
             * Verifica se o campo "title" existe, e se sim, pega seu valor.
             */
            var title: String? = null
            if (content["title"] != null) {
                title = content["title"].asString
            }

            /*
             * Verifica se o campo "summary" existe, e se sim, pega seu valor.
             */
            var summary: String? = null
            if (content["summary"] != null) {
                summary = content["summary"].asString
            }

            /*
             * Verifica se o campo "url" existe, e se sim, pega seu valor.
             */
            var url: String? = null
            if (content["url"] != null) {
                url = content["url"].asString
            }

            /*
             * Verifica se o campo "age" existe no item, e se sim, pega seu valor.
             */
            var age: Long? = null
            if (item.asJsonObject["age"] != null) {
                age = item.asJsonObject["age"].asLong
            }

            /*
             * Com todos os campos deserializados, cria-se um [News] com os dados
             * e então adiciona-o à lista.
             */
            val news = News(image, chapeu, title, summary, url, age)
            items.add(news)
        }

        /* Retorna um [NewsFetchResponse], com as notícias [items] obtidas da API */
        return NewsFetchResponse(items)
    }
}