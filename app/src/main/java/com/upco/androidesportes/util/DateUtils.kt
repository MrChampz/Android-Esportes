package com.upco.androidesportes.util

import android.content.Context
import com.upco.androidesportes.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

/**
 * Fornece métodos estáticos para a conversão e exibição de timestamps.
 */
object DateUtils {

    /*
     * Padrão usado para converter o timestamp vindo da API em um formato de
     * data usado no Android.
     * yyyy -> Ano com quatro caracteres (ex. 2019).
     * MM -> Mês com dois caracteres (ex. 02).
     * dd -> Dia com dois caracteres (ex. 17).
     * HH -> Horas com dois caracteres (ex. 22).
     * mm -> Minutos com dois caracteres (ex. 36).
     * ss -> Segundos com dois caracteres (ex. 44).
     * SSS -> Milissegundos com três caracteres (ex. 895).
     * 'Z' -> Indica que o timestamp está normalizado em UTC.
     */
    private const val TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    /*
     * Salva o timezone padrão do sistema antes de alterar para UTC. Isso é necessário,
     * pois para exibir o timestamp de forma correta posteriormente, o mesmo deve ser
     * convertido de UTC para o timezone padrão do sistema. Dessa forma o timestamp em
     * UTC será salvo no banco de dados e apenas em sua exibição será convertido para o
     * timezone do usuário.
     */
    private val defaultTimeZone = TimeZone.getDefault()

    /**
     * Bloco init, o código contido aqui será executado assim que algum método desse object
     * for chamado. A primeira vez que algum método de algum object é chamado, uma instância
     * singleton desse object é criada, e o bloco init desse object é executado.
     */
    init {
        /*
         * Define o timezone padrão do sistema como UTC, pois todas
         * as operações com datas devem ser feitas nesse timezone.
         */
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    /**
     * Recebe um [timestamp] no formato [yyyy-MM-ddTHH:mm:ss.SSSZ] (UTC), e converte
     * em um timestamp em milissegundos, que pode ser utilizado no banco de dados.
     *
     * @param timestamp String contendo o timestamp no formato especificado.
     * @return Retorna a data e hora UTC, em milissegundos.
     */
    fun getUtcFromTimestamp(timestamp: String): Long {
        val fmt = SimpleDateFormat(TIMESTAMP_PATTERN, Locale("pt", "BR"))
        return fmt.parse(timestamp).time
    }

    /**
     * Método auxiliar para converter a representação do timestamp no banco de dados em algo
     * exibível aos usuários. Por mais elegante e polido que uma experiencia do usuário como
     * "1474061664" seja, podemos fazer melhor.
     * <p/>
     * A anotação @JvmStatic é utilizada para que o método possa ser utilizado por meio de
     * DataBinding.
     * <p/>
     * A lógica usada para a exibição do timestamp é a seguinte:
     * Até um segundo: "1 segundo atrás"
     * Mais de um segundo: "10 segundos atrás"
     * Até um minuto: "1 minuto atrás"
     * Mais de um minuto: "20 minutos atrás"
     * Até uma hora: "1 hora atrás"
     * Mais de uma hora: "2 horas atrás"
     * Até um dia: "1 dia atrás"
     * Mais de um dia: "4 dias atrás"
     * Em caso de erro: "Ooops"
     *
     * @param context       Contexto usado para a localização de recursos (strings).
     * @param normalizedUtc O timestamp em UTC+0.
     * @return Uma representação do timestamp de forma amigável ao usuário, como "1 segundo atrás",
     *         "20 minutos atrás" ou "4 dias atrás".
     */
    @JvmStatic
    fun getFriendlyTimeString(context: Context, normalizedUtc: Long): String {
        /*
         * Como o timestamp inserido no banco de dados é normalizado (UTC), precisamos
         * pega-lo e transformar em um timestamp no timezone local do usuário.
         */
        val timestamp = getLocalFromNormalizedUtcDate(normalizedUtc)

        /*
         * O timezone padrão do sistema foi alterado para UTC, fica mais fácil de fazer
         * operações com data e hora em UTC, pois será sempre a mesma independente de
         * onde o dispositivo estiver localizado. Sendo assim, a data e hora atual
         * retornada por Date estará em UTC, por isso utilizamos o método
         * getLocalFromNormalizedUtcDate para converter de UTC para o timezone local do
         * usuário, assim as operações abaixo serão feitas corretamente.
         */
        val now = getLocalFromNormalizedUtcDate(Date().time)

        /*
         * Calcula a diferença entre a data atual e a data passada como parâmetro.
         * Esse calculo é feito em milissegundos, para converter em segundos, divide
         * o resultado por 1000. Por fim, arredonda o resultado com a função [floor].
         */
        val seconds = floor(((now - timestamp)  / 1000).toFloat())

        /*
         * Calcula o intervalo, dividindo a quantidade de segundos da data pela quantidade
         * de segundos que há em um ano (31536000).
         * Caso o resultado seja igual à 1, passou-se um ano e a string retornada estará no
         * singular. Caso o resultado seja maior que 1, passou-se mais de um ano e a string
         * retornada estará no plural.
         */
        var interval = floor(seconds / 31536000).toInt()
        if (interval == 1) {
            return context.getString(R.string.tx_timestamp_year, interval)
        } else if (interval > 1) {
            return context.getString(R.string.tx_timestamp_years, interval)
        }

        /*
         * Calcula o intervalo, dividindo a quantidade de segundos da data pela quantidade
         * de segundos que há em um mês (2592000).
         * Caso o resultado seja igual à 1, passou-se um mês e a string retornada estará no
         * singular. Caso o resultado seja maior que 1, passou-se mais de um mês e a string
         * retornada estará no plural.
         */
        interval = floor(seconds / 2592000).toInt()
        if (interval == 1) {
            return context.getString(R.string.tx_timestamp_month, interval)
        } else if (interval > 1) {
            return context.getString(R.string.tx_timestamp_months, interval)
        }

        /*
         * Calcula o intervalo, dividindo a quantidade de segundos da data pela quantidade
         * de segundos que há em um dia (86400).
         * Caso o resultado seja igual à 1, passou-se um dia e a string retornada estará no
         * singular. Caso o resultado seja maior que 1, passou-se mais de um dia e a string
         * retornada estará no plural.
         */
        interval = floor(seconds / 86400).toInt()
        if (interval == 1) {
            return context.getString(R.string.tx_timestamp_day, interval)
        } else if (interval > 1) {
            return context.getString(R.string.tx_timestamp_days, interval)
        }

        /*
         * Calcula o intervalo, dividindo a quantidade de segundos da data pela quantidade
         * de segundos que há em uma hora (3600).
         * Caso o resultado seja igual à 1, passou-se uma hora e a string retornada estará
         * no singular. Caso o resultado seja maior que 1, passou-se mais de uma hora e a
         * string retornada estará no plural.
         */
        interval = floor(seconds / 3600).toInt()
        if (interval == 1) {
            return context.getString(R.string.tx_timestamp_hour, interval)
        } else if (interval > 1) {
            return context.getString(R.string.tx_timestamp_hours, interval)
        }

        /*
         * Calcula o intervalo, dividindo a quantidade de segundos da data pela quantidade
         * de segundos que há em um minuto (60).
         * Caso o resultado seja igual à 1, passou-se um minuto e a string retornada estará
         * no singular. Caso o resultado seja maior que 1, passou-se mais de um minuto e a
         * string retornada estará no plural.
         */
        interval = floor(seconds / 60).toInt()
        if (interval == 1) {
            return context.getString(R.string.tx_timestamp_minute, interval)
        } else if (interval > 1) {
            return context.getString(R.string.tx_timestamp_minutes, interval)
        }

        /*
         * Calcula o intervalo, como o valor já está em segundos, simplesmente arredonda.
         * Caso o resultado seja igual à 1, passou-se um segundo e a string retornada estará
         * no singular. Caso o resultado seja maior que 1, passou-se mais de um segundo e a
         * string retornada estará no plural.
         */
        interval = floor(seconds).toInt()
        if (interval == 1) {
            return context.getString(R.string.tx_timestamp_second, interval)
        } else if (interval > 1) {
            return context.getString(R.string.tx_timestamp_seconds, interval)
        }

        /* Em caso de erro, retorna a string tx_timestamp_error */
        return context.getString(R.string.tx_timestamp_error)
    }

    /**
     * Esse método retornará o timestamp local para o timestamp UTC+0 (normalizado) fornecido.
     *
     * @param normalizedUtcDate Timestamp em UTC+0. Esse número é fornecido pelo banco de dados.
     * @return O timestamp local correspondente ao timestamp em UTC+0 fornecido.
     */
    private fun getLocalFromNormalizedUtcDate(normalizedUtcDate: Long): Long {
        /*
         * O timezone nos fornecerá o offset que deve ser aplicado ao timestamp em UTC,
         * para que o mesmo possa ser convertido no timezone do usuário.
         * Esse offset, em milissegundos, quando adicionado ao timestamp em UTC,
         * resultará no timestamp local.
         */
        val gmtOffset = defaultTimeZone.getOffset(normalizedUtcDate)

        /* Retorna o timestamp local (em milissegundos) */
        return normalizedUtcDate + gmtOffset
    }
}