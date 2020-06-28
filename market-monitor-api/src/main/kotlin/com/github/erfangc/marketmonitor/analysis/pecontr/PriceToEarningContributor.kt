package com.github.erfangc.marketmonitor.analysis.pecontr

data class PriceToEarningContributor(
        val ticker: String,
        val name: String,
        val peContribution: Double,
        val sector: String,
        val industry: String,
        val pe: Double,
        val marketCap: Double
)