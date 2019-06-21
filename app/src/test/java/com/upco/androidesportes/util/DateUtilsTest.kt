package com.upco.androidesportes.util


import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.ParseException

/**
 * Testes unitários para o objeto [DateUtils].
 */
class DateUtilsTest {

    @Test
    fun getUtcFromTimestamp_CorrectTimestamp_ReturnsUtcLong() {
        var timestamp = "2019-06-08T01:50:26.453Z"
        var millis = 1559958626453L
        var utc = DateUtils.getUtcFromTimestamp(timestamp)
        assertEquals(millis, utc)

        timestamp = "2019-06-09T22:56:00.000Z"
        millis = 1560120960000L
        utc = DateUtils.getUtcFromTimestamp(timestamp)
        assertEquals(millis, utc)

        timestamp = "2019-06-07T14:56:26.354Z"
        millis = 1559919386354L
        utc = DateUtils.getUtcFromTimestamp(timestamp)
        assertEquals(millis, utc)

        timestamp = "2019-06-08T04:32:14.151Z"
        millis = 1559968334151L
        utc = DateUtils.getUtcFromTimestamp(timestamp)
        assertEquals(millis, utc)
    }

    @Test(expected = ParseException::class)
    fun getUtcFromTimestamp_InvalidTimestamp_ShouldThrowException() {
        /* Timestamp sem o caractere 'T' */
        var timestamp = "2019-06-08 04:32:14.151Z"
        DateUtils.getUtcFromTimestamp(timestamp)

        /* Timestamp sem o caractere 'Z' */
        timestamp = "2019-06-08T04:32:14.151"
        DateUtils.getUtcFromTimestamp(timestamp)

        /* Timestamp sem os caracteres 'T' e 'Z' */
        timestamp = "2019-06-08 04:32:14.151"
        DateUtils.getUtcFromTimestamp(timestamp)
    }
}