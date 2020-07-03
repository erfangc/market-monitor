package com.github.erfangc.marketmonitor.quandl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.erfangc.marketmonitor.io.ApiKeys.quandlApiKey
import com.github.erfangc.marketmonitor.quandl.models.QuandlTable
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@ExperimentalStdlibApi
class QuandlService(private val objectMapper: ObjectMapper) {

    private val log = LoggerFactory.getLogger(QuandlService::class.java)

    fun exportQuandl(
            publisher: String,
            table: String,
            callback: (QuandlTable) -> Unit
    ) {
        var cursorId: String? = null
        var batch = 1
        do {
            val quandlResponse = queryQuandl(publisher, table, cursorId)
            callback.invoke(quandlResponse)
            log.info("Finished processing batch $batch")
            batch++
            cursorId = quandlResponse.meta.next_cursor_id
        } while (cursorId != null)

    }

    private fun queryQuandl(publisher: String, table: String, cursorId: String?): QuandlTable {
        val uri = ("https://www.quandl.com/api/v3/datatables/$publisher/$table.json?&api_key=${quandlApiKey}"
                + (cursorId?.let { "&qopts.cursor_id=$it" } ?: ""))
        val httpGet = HttpGet(uri)
        log.info("Requesting table data from Quandl via $uri")
        val inputStream = HttpClientBuilder
                .create()
                .build()
                .execute(httpGet)
                .entity
                .content
        return objectMapper.readValue(inputStream, QuandlTable::class.java)
    }

}