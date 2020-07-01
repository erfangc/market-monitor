package com.github.erfangc.marketmonitor.analysis.companyreturn.models

import com.github.erfangc.marketmonitor.tickers.Ticker
import java.time.LocalDate

data class CompanyReturnAnalysis(
        val _id: String,
        val ticker: String,
        val date: LocalDate,
        val meta: Ticker,
        val pricingFunctionOutputs: PricingFunctionOutputs,
        val shortTermEpsGrowths: List<ShortTermEpsGrowth>,
        val longTermGrowth: Double,
        val bvps: Double,
        val eps: Double,
        val priceToEarning: Double,
        val scenarios: List<Scenario> = emptyList(),
        val discountRate: Double
)
