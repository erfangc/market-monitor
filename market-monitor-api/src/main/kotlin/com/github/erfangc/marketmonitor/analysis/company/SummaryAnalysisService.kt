package com.github.erfangc.marketmonitor.analysis.company

import com.github.erfangc.marketmonitor.analysis.company.models.CompanyAnalysis
import com.github.erfangc.marketmonitor.quandl.QuandlService.downloadTable
import org.nield.kotlinstatistics.median
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


data class SectorSummary(
        val name: String,
        val medianPctValueFromGrowth: Double,
        val medianPctValueFromLongTerm: Double,
        val medianPctValueFromBook: Double,
        val avgPctValueFromGrowth: Double,
        val avgPctValueFromLongTerm: Double,
        val avgPctValueFromBook: Double
)

@Service
@ExperimentalStdlibApi
class CompanyReturnSummaryService {

    private val log = LoggerFactory.getLogger(CompanyReturnSummaryService::class.java)

    fun get(): Unit {

        //
        // sector analyses
        //
        val results = CompanyAnalysisService.companyReturnAnalysesCollection.find().toList()
        val byTicker = results.associateBy { it.ticker }

        val sectorReturnAnalyses = results
                .groupBy { it.meta.sector }
                .map {

                    entry ->

                    val pctValueFromGrowth = entry
                            .value
                            .mapNotNull { it.pricingFunctionOutputs.contributionFromGrowth / it.pricingFunctionOutputs.price }
                            .filterNot { it.isNaN() }

                    val pctValueFromLongTerm = entry
                            .value
                            .mapNotNull { it.pricingFunctionOutputs.contributionFromTerminalValue / it.pricingFunctionOutputs.price }
                            .filterNot { it.isNaN() }

                    val pctValueFromBook = entry
                            .value
                            .mapNotNull { it.pricingFunctionOutputs.contributionFromBvps / it.pricingFunctionOutputs.price }
                            .filterNot { it.isNaN() }

                    SectorSummary(
                            name = entry.key ?: "N/A",
                            avgPctValueFromBook = pctValueFromBook.average(),
                            avgPctValueFromGrowth = pctValueFromGrowth.average(),
                            avgPctValueFromLongTerm = pctValueFromLongTerm.average(),
                            medianPctValueFromBook = pctValueFromBook.median(),
                            medianPctValueFromGrowth = pctValueFromGrowth.median(),
                            medianPctValueFromLongTerm = pctValueFromLongTerm.median()
                    )
                }

        val snp500 = downloadTable(publisher = "SHARADAR", table = "SNP500")
                .asList()
                .mapNotNull {
                    it["ticker"]?.let { ticker ->
                        byTicker[ticker]
                    }
                }

        val top20 = snp500.sortedByDescending { it.discountRate }.take(20)
        val bottom20 = snp500.sortedBy { it.discountRate }.take(20)

    }
}

data class MarketSummary2(
        val sectorSummaries: SectorSummary,
        val top20Expected: List<CompanyAnalysis>,
        val bottom20Expected: List<CompanyAnalysis>
)