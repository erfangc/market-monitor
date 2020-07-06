package com.github.erfangc.marketmonitor.analysis.summary.models

import com.github.erfangc.marketmonitor.analysis.company.models.CompanyAnalysis

data class SectorSummary(
        val name: String,
        val top20DiscountRate: List<CompanyAnalysis>,
        val bottom20DiscountRate: List<CompanyAnalysis>,
        val discountRate: SummaryDescription,
        val pctValueFromGrowth: SummaryDescription,
        val pctValueFromTbvps: SummaryDescription,
        val pctValueFromCurrentEarnings: SummaryDescription,
        val pctValueFromTerminalValue: SummaryDescription
)
