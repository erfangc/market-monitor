package com.github.erfangc.marketmonitor.io

import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder

object HttpClient {
    val httpClient: CloseableHttpClient = HttpClientBuilder.create().build()
}