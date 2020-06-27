package com.github.erfangc.marketmonitor.tickers

import java.time.LocalDate

data class Ticker(
        val table: String,
        val permaticker: String,
        val ticker: String,
        val name: String? = null,
        val exchange: String? = null,
        val isdelisted: String? = null,
        val category: String? = null,
        val cusips: String? = null,
        val siccode: String? = null,
        val sicsector: String? = null,
        val sicindustry: String? = null,
        val famasector: String? = null,
        val famaindustry: String? = null,
        val sector: String? = null,
        val industry: String? = null,
        val scalemarketcap: String? = null,
        val scalerevenue: String? = null,
        val relatedtickers: String? = null,
        val currency: String? = null,
        val location: String? = null,
        val lastupdated: LocalDate? = null,
        val firstadded: LocalDate? = null,
        val firstpricedate: LocalDate? = null,
        val lastpricedate: LocalDate? = null,
        val firstquarter: LocalDate? = null,
        val lastquarter: LocalDate? = null,
        val secfilings: String? = null,
        val companysite: String? = null
)

data class TickerRow(
        val _id: String,
        val ticker: Ticker
)