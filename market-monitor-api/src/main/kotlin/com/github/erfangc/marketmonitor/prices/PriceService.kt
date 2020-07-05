package com.github.erfangc.marketmonitor.prices

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.erfangc.marketmonitor.io.ApiKeys.quandlApiKey
import com.github.erfangc.marketmonitor.io.HttpClient.httpClient
import com.github.erfangc.marketmonitor.previousWorkingDay
import com.github.erfangc.marketmonitor.quandl.models.QuandlTable
import org.apache.http.client.methods.HttpGet
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDate

@ExperimentalStdlibApi
@Service
class PriceService {

    private val objectMapper = jacksonObjectMapper()

    @Cacheable("getPrice")
    fun getPrice(ticker: String, date: LocalDate? = null): Double {
        val date = LocalDate.now().previousWorkingDay()
        val get = HttpGet(
                "https://www.quandl.com/api/v3/datatables/SHARADAR/SEP.json?ticker=$ticker" +
                        "&api_key=$quandlApiKey&date.gte=$date&date.lte=$date"
        )
        val inputStream = httpClient.execute(get).entity.content
        val quandlTable = objectMapper.readValue(inputStream, QuandlTable::class.java)
        return quandlTable
                .asList()
                .firstOrNull()
                ?.get("close")
                ?.toDoubleOrNull() ?: error("Unable to find the price for $ticker on $date in Quandl")
    }
}