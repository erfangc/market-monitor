package com.github.erfangc.marketmonitor.analysis.company

import com.github.erfangc.marketmonitor.analysis.company.models.CompanyAnalysis
import com.github.erfangc.marketmonitor.analysis.company.models.CompanyAnalysisRequest
import com.github.erfangc.marketmonitor.analysis.company.models.PricingFunctionInputs
import com.github.erfangc.marketmonitor.analysis.company.models.ShortTermEpsGrowth
import com.github.erfangc.marketmonitor.assets.AssetsService
import com.github.erfangc.marketmonitor.dailymetrics.DailyMetricsService
import com.github.erfangc.marketmonitor.fundamentals.FundamentalsService
import com.github.erfangc.marketmonitor.fundamentals.models.Fundamental
import com.github.erfangc.marketmonitor.io.MongoDB.database
import com.github.erfangc.marketmonitor.previousWorkingDay
import com.github.erfangc.marketmonitor.prices.PriceService
import com.github.erfangc.marketmonitor.yfinance.YFinanceService
import com.mongodb.client.model.Indexes
import org.litote.kmongo.getCollection
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@ExperimentalStdlibApi
class CompanyAnalysisService(
        private val fundamentalsService: FundamentalsService,
        private val assetsService: AssetsService,
        private val yFinanceService: YFinanceService,
        private val dailyMetricsService: DailyMetricsService,
        private val priceService: PriceService
) {

    private val log = LoggerFactory.getLogger(CompanyAnalysisService::class.java)

    companion object {
        val companyAnalysesCollection = database.getCollection<CompanyAnalysis>()
    }

    fun bootstrap() {
        log.info("Bootstrapping company analysis and saving the results to MongoDB")
        val date = LocalDate.now().previousWorkingDay()
        log.info("Performing expected company analysis on $date")
        val rows = dailyMetricsService
                .getDailyMetrics(date)
                .filter { (it.marketcap ?: 0.0) > 500 }
                .mapNotNull { dailyMetric ->
                    val ticker = dailyMetric.ticker
                    log.info("Analyzing expected returns for $ticker as of $date")
                    try {
                        val request = getCompanyAnalysisRequest(ticker)
                        companyAnalysis(request)
                    } catch (e: Exception) {
                        log.error("Unable to compute expected return analysis for $ticker on $date, message=${e.message}")
                        null
                    }
                }
        companyAnalysesCollection.drop()
        companyAnalysesCollection.createIndex(
                Indexes.ascending(CompanyAnalysis::date.name)
        )
        val result = companyAnalysesCollection.insertMany(rows)
        log.info("Saved ${result.insertedIds.size} rows into MongoDB collection ${CompanyAnalysis::class.simpleName}")
    }

    fun getCompanyAnalysisRequest(ticker: String): CompanyAnalysisRequest {
        val rows = yFinanceService.getEarningsEstimate(ticker)
        val row = rows.find { it["Earnings Estimate"] == "Avg. Estimate" } ?: emptyMap()

        val now = LocalDate.now().previousWorkingDay()
        val shortTermEpsGrowths = try {
            tryParsingEpsForCurrentFY(currentFy = now, row = row)
        } catch (e: IllegalStateException) {
            tryParsingEpsForCurrentFY(currentFy = now.plusYears(1), row = row)
        }

        return CompanyAnalysisRequest(
                ticker = ticker,
                date = now,
                longTermGrowth = 0.03,
                shortTermEpsGrowths = shortTermEpsGrowths,
                price = priceService.getPrice(ticker)
        )

    }

    /**
     * Compute the discount rate required to equate price to today's price
     * given all the assumptions
     */
    fun companyAnalysis(request: CompanyAnalysisRequest): CompanyAnalysis {
        val date = request.date ?: LocalDate.now().previousWorkingDay()
        val ticker = request.ticker
        val mrt = fundamentalsService.getMRT(ticker = ticker, notAfter = date)
        val longTermGrowth = request.longTermGrowth
        val dailyMetrics = dailyMetricsService.getDailyMetric(ticker, date)

        if (mrt.isEmpty()) {
            error("Dimension MRT Fundamental data is unavailable for $ticker on $date")
        }
        val fundamental = mrt.last()

        val eps = fundamental.epsusd ?: error("EPS is unavailable for $ticker on $date")
        val tbvps = tbvps(fundamental) ?: 0.0
        val fx = fundamental.fxusd ?: 1.0
        val price = request.price ?: fundamental.price ?: error("Price is unavailable for $ticker on $date")

        if (price == 0.0) {
            error("Price for $ticker cannot be 0.0")
        }

        val shortTermEpsGrowths = request.shortTermEpsGrowths.map {
            it.copy(eps = it.eps / fx)
        }

        val discountRate = bisection(initialMin = longTermGrowth, initialMax = 1.0) { discountRate ->
            pricingFunction(
                    PricingFunctionInputs(
                            date = date,
                            eps = eps,
                            tbvps = tbvps,
                            discountRate = discountRate,
                            longTermGrowth = longTermGrowth,
                            shortTermEpsGrowth = shortTermEpsGrowths
                    )
            ).price - price
        }

        val pricingFunctionOutputs = pricingFunction(
                PricingFunctionInputs(
                        date = date,
                        tbvps = tbvps,
                        eps = eps,
                        longTermGrowth = longTermGrowth,
                        discountRate = discountRate,
                        shortTermEpsGrowth = shortTermEpsGrowths
                )
        )

        val meta = assetsService.getTicker(ticker) ?: error("Unable to find information for $ticker")

        return CompanyAnalysis(
                _id = "$ticker:$date",
                meta = meta,
                date = date,
                ticker = request.ticker,
                eps = eps,
                bvps = tbvps,
                marketcap = dailyMetrics?.marketcap ?: 0.0,
                ev = dailyMetrics?.ev ?: 0.0,
                evebitda = dailyMetrics?.evebitda ?: 0.0,
                evebit = dailyMetrics?.evebit ?: 0.0,
                pb = dailyMetrics?.pb ?: 0.0,
                ps = dailyMetrics?.ps ?: 0.0,
                priceToEarning = price / eps,
                shortTermEpsGrowths = shortTermEpsGrowths,
                discountRate = discountRate,
                longTermGrowth = longTermGrowth,
                pricingFunctionOutputs = pricingFunctionOutputs
        )
    }

    private fun tryParsingEpsForCurrentFY(
            currentFy: LocalDate,
            row: Map<String, String>
    ): List<ShortTermEpsGrowth> {
        val forYear = { date: LocalDate ->
            val adjective = if (date.year == currentFy.year) "Current" else "Next"
            "$adjective Year (${date.year})"
        }
        val nextYear = currentFy.plusYears(1)
        return listOf(currentFy, nextYear).map { date ->
            ShortTermEpsGrowth(
                    date = date,
                    eps = row[forYear(date)]?.toDouble()
                            ?: error("Unable to find EPS estimate for ${date.year}")
            )
        }
    }

    /**
     * We compute the Tangible Book Value per Share
     * as the per share value derived by subtracting intangibles from total asset
     * then subtracting liabilities
     */
    private fun tbvps(fundamental: Fundamental): Double? {
        val assets = fundamental.assets
        val liabilities = fundamental.liabilities
        val intangibles = fundamental.intangibles ?: 0.0
        val shareswa = fundamental.shareswa
        val fx = fundamental.fxusd ?: 1.0
        return if (assets != null && liabilities != null && shareswa != null) {
            ((assets - liabilities - intangibles) / shareswa) / fx
        } else {
            null
        }
    }

}