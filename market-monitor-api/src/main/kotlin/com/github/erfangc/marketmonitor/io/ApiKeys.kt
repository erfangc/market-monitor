package com.github.erfangc.marketmonitor.io

object ApiKeys {
    val quandlApiKey = System.getenv("QUANDL_API_KEY")
            ?: error("QUANDL_API_KEY missing from environment")
}