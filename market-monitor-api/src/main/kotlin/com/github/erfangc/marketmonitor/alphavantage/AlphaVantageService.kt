package com.github.erfangc.marketmonitor.alphavantage

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.erfangc.marketmonitor.io.HttpClient.httpClient
import org.apache.http.client.methods.HttpGet
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class AlphaVantageService(
        private val objectMapper: ObjectMapper
) {

    private val apiKey = System.getenv("ALPHA_VANTAGE_API_KEY")
            ?: error("environment ALPHA_VANTAGE_API_KEY is not defined")

    @Cacheable("getPrice")
    fun getPrice(ticker: String): Double {
        val inputStream = httpClient
                .execute(HttpGet("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=$ticker&apikey=$apiKey"))
                .entity
                .content
        val tree = objectMapper.readTree(inputStream)
        return tree
                .at("/Global Quote/05. price")
                .asDouble()
    }

}