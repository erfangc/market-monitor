package com.github.erfangc.marketmonitor.analysis.summary.models

import com.github.erfangc.marketmonitor.analysis.company.models.CompanyAnalysis

data class SummaryAnalysis(
        val sectorSummaries: List<SectorSummary>,
        val snp500TopDiscountRates: List<CompanyAnalysis>,
        val snp500BottomDiscountRates: List<CompanyAnalysis>
)