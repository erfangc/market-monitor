package com.github.erfangc.marketmonitor.analysis.company.models

import com.github.erfangc.marketmonitor.assets.Asset
import java.time.LocalDate

data class CompanyAnalysis(
        val _id: String,
        val ticker: String,
        val date: LocalDate,
        val meta: Asset,
        val longTermGrowth: Double,
        val bvps: Double,
        val eps: Double,
        val marketcap: Double,
        val ev: Double,
        val evebitda: Double,
        val evebit: Double,
        val pb: Double,
        val ps: Double,
        val priceToEarning: Double,
        val discountRate: Double,
        val shortTermEpsGrowths: List<ShortTermEpsGrowth>,
        val pricingFunctionOutputs: PricingFunctionOutputs
)
