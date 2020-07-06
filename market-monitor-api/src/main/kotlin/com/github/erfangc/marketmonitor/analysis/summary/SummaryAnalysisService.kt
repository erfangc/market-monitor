package com.github.erfangc.marketmonitor.analysis.summary

import com.github.erfangc.marketmonitor.analysis.company.CompanyAnalysisService
import com.github.erfangc.marketmonitor.analysis.company.models.CompanyAnalysis
import com.github.erfangc.marketmonitor.analysis.summary.models.SectorSummary
import com.github.erfangc.marketmonitor.analysis.summary.models.SummaryAnalysis
import com.github.erfangc.marketmonitor.analysis.summary.models.SummaryDescription
import com.github.erfangc.marketmonitor.quandl.QuandlService.downloadTable
import org.nield.kotlinstatistics.median
import org.nield.kotlinstatistics.skewness
import org.nield.kotlinstatistics.standardDeviation
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@ExperimentalStdlibApi
class CompanyReturnSummaryService(private val companyAnalysisService: CompanyAnalysisService) {

    private val log = LoggerFactory.getLogger(CompanyReturnSummaryService::class.java)

    fun getSummaryAnalysis(): SummaryAnalysis {
        var results = CompanyAnalysisService.companyReturnAnalysesCollection.find().toList()
        if (results.isEmpty()) {
            log.info("Performing market summary analysis by bootstrapping company analysis")
            companyAnalysisService.bootstrap()
            results = CompanyAnalysisService.companyReturnAnalysesCollection.find().toList()
        }
        val sectorSummaries = sectorSummaries(results)
        val snp500 = snp500(results).sortedBy { it.discountRate }
        val snp500TopDiscountRates = snp500.takeLast(20)
        val snp500BottomDiscountRates = snp500.take(20)

        return SummaryAnalysis(
                sectorSummaries = sectorSummaries,
                snp500BottomDiscountRates = snp500BottomDiscountRates,
                snp500TopDiscountRates = snp500TopDiscountRates
        )
    }

    private fun sectorSummaries(results: List<CompanyAnalysis>): List<SectorSummary> {
        return results
                .groupBy { it.meta.sector }
                .map { entry ->
                    val analyses = entry.value

                    val pctValueFromGrowth = analyses
                            .mapNotNull { it.pricingFunctionOutputs.contributionFromGrowth / it.pricingFunctionOutputs.price }
                            .filterNot { it.isNaN() }

                    val pctValueFromTerminalValue = analyses
                            .mapNotNull { it.pricingFunctionOutputs.contributionFromTerminalValue / it.pricingFunctionOutputs.price }
                            .filterNot { it.isNaN() }

                    val pctValueFromTbvps = analyses
                            .mapNotNull { it.pricingFunctionOutputs.contributionFromBvps / it.pricingFunctionOutputs.price }
                            .filterNot { it.isNaN() }

                    val pctValueFromCurrentEarnings = analyses
                            .mapNotNull { it.pricingFunctionOutputs.contributionFromCurrentEarnings / it.pricingFunctionOutputs.price }
                            .filterNot { it.isNaN() }

                    val discountRate = analyses.map { it.discountRate }

                    val top20DiscountRate = analyses.sortedByDescending { it.discountRate }.take(20)
                    val bottom20DiscountRate = analyses.sortedBy { it.discountRate }.take(20)

                    SectorSummary(
                            name = entry.key ?: "N/A",
                            top20DiscountRate = top20DiscountRate,
                            bottom20DiscountRate = bottom20DiscountRate,
                            discountRate = summaryDescription(discountRate),
                            pctValueFromCurrentEarnings = summaryDescription(pctValueFromCurrentEarnings),
                            pctValueFromGrowth = summaryDescription(pctValueFromGrowth),
                            pctValueFromTbvps = summaryDescription(pctValueFromTbvps),
                            pctValueFromTerminalValue = summaryDescription(pctValueFromTerminalValue)
                    )
                }
    }

    private fun snp500(results: List<CompanyAnalysis>): List<CompanyAnalysis> {
        val byTicker = results.associateBy { it.ticker }
        return downloadTable(publisher = "SHARADAR", table = "SP500")
                .asList()
                .mapNotNull { it["ticker"] }
                .distinct()
                .mapNotNull { byTicker[it] }
    }

    private fun summaryDescription(values: List<Double>): SummaryDescription {
        return SummaryDescription(
                median = values.median(),
                max = values.max() ?: error(""),
                min = values.min() ?: error(""),
                average = values.average(),
                skew = values.skewness,
                stddev = values.standardDeviation()
        )
    }

}

