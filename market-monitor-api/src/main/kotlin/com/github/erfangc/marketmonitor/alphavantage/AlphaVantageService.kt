package com.github.erfangc.marketmonitor.alphavantage

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.erfangc.marketmonitor.io.ApiKeys.alphaVantageApiKey
import com.github.erfangc.marketmonitor.io.HttpClient.httpClient
import org.apache.http.client.methods.HttpGet
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class AlphaVantageService(private val objectMapper: ObjectMapper) {

    @Cacheable("getPrice")
    fun getPrice(ticker: String): Double {
        val get = HttpGet("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=$ticker&apikey=$alphaVantageApiKey")
        val inputStream = httpClient.execute(get).entity.content
        val tree = objectMapper.readTree(inputStream)
        return tree
                .at("/Global Quote/05. price")
                .asDouble()
    }

}