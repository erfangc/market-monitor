package com.github.erfangc.marketmonitor.analysis

import java.time.LocalDate

data class MarketSummary(
        val _id: String,
        val date: LocalDate,
        val positiveNegativeRatio: Double,
        val pe: Double,
        val peCount: Int,
        val totalMarketCap: Double
)