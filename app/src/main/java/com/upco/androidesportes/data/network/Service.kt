package com.upco.androidesportes.data.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.upco.androidesportes.model.News
import com.upco.androidesportes.model.NewsFetchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private val TAG = Service::class.java.simpleName

/**
 * Função estática que obtém as notícias da API, usando o [service].
 *
 * @param service Instância de [Service], que será usada como interface para API.
 *
 * Faz uma requisição à API com os seguintes parâmetros:
 *
 * @param page Índice da página, com notícias, a ser obtida, partindo do índice 1.
 *
 * O resultado da requisição é manipulado pela implementação das funções passadas
 * como parâmetro:
 *
 * @param onSuccess Função que define como manipular a lista de notícias recebida.
 * @param onError Função que define como manipular uma falha na requisição.
 */
fun fetchNews(
    service: Service,
    page: Int,
    onSuccess: (news: List<News>) -> Unit,
    onError: (error: String) -> Unit
) {
    Log.d(TAG, "Página: $page")

    /*
     * Usa o [service] para obter as notícias da API.
     * Em caso de sucesso, retorna as notícias por meio do callback [onSuccess].
     * Em caso de erro, retorna o erro por meio do callback [onError].
     */
    service.fetchNews(page).enqueue(
        object: Callback<NewsFetchResponse> {

            override fun onResponse(
                call: Call<NewsFetchResponse>,
                response: Response<NewsFetchResponse>
            ) {
                Log.d(TAG, "Resposta obtida: $response")
                if (response.isSuccessful) {
                    /* Em caso de sucesso, retorna as notícias */
                    val news = response.body()?.items ?: emptyList()
                    onSuccess(news)
                } else {
                    /* Em caso de erro, retorna o erro */
                    onError(response.errorBody()?.string() ?: "Erro desconhecido")
                }
            }

            override fun onFailure(call: Call<NewsFetchResponse>, t: Throwable) {
                Log.d(TAG, "Falha ao obter os dados")
                onError(t.message ?: "Erro desconhecido")
            }
        }
    )
}

/**
 * Configuração da comunicação com a API via Retrofit.
 */
interface Service {

    /**
     * Obtém as notícias de uma página [page] específica.
     * @param page Índice da página a ser obtida, partindo de 1.
     * @return Callback com os dados [NewsFetchResponse], ou com os erros, retornados pela API.
     */
    @GET("feeds/b904b430-123a-4f93-8cf4-5365adf97892/posts/page/{page}")
    fun fetchNews(
        @Path("page") page: Int
    ): Call<NewsFetchResponse>

    companion object {
        /* Url base da API, todas as requisições serão feitas a partir dessa url. */
        private const val BASE_URL = "http://falkor-cda.bastian.globo.com/"

        /**
         * Inicializa o Retrofit, cria e retorna uma instância de [Service].
         * @return Instância de [Service] criada e configurada.
         */
        fun create(): Service {
            /*
             * Configura o logging interceptor, que tem a função de emitir logs
             * sobre as requisições HTTP e os dados das respostas.
             */
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            /*
             * Configura o cliente com o logging interceptor.
             */
            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()

            /*
             * Cria um [Gson] que será passado como parâmetro no método [create] do
             * [GsonConverterFactory]. Dessa forma podemos usar deserializers implementados por nós,
             * como o [Deserializer], que converte a resposta da API em um [NewsFetchResponse].
             */
            val gson = GsonBuilder()
                    .registerTypeAdapter(NewsFetchResponse::class.java, Deserializer())
                    .create()

            /*
             * Inicialize o Retrofit com todas as configurações necessárias
             * e então cria a instância de [Service] por meio do mesmo.
             */
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(Service::class.java)
        }
    }
}