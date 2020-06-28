package com.github.erfangc.marketmonitor.analysis.marketsummary

import java.time.LocalDate

data class SubMarketSummary(
        val name: String,
        val medianPe: Double,
        val marketCapWeightedPe: Double,
        val marketCap: Double,
        val tickerCount: Int
)

data class MarketSummary(
        val _id: String,
        val date: LocalDate,
        val positiveNegativeRatio: Double,
        val pe: Double,
        val peCount: Int,
        val totalMarketCap: Double,
        val sectorSummaries: List<SubMarketSummary>
)
