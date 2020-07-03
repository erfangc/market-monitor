package com.github.erfangc.marketmonitor.analysis.companyreturn

import com.github.erfangc.marketmonitor.analysis.companyreturn.models.CompanyReturnAnalysis
import com.github.erfangc.marketmonitor.analysis.companyreturn.models.CompanyReturnAnalysisRequest
import com.github.erfangc.marketmonitor.analysis.companyreturn.models.PricingFunctionInputs
import com.github.erfangc.marketmonitor.analysis.companyreturn.models.ShortTermEpsGrowth
import com.github.erfangc.marketmonitor.dailymetrics.DailyMetricsService
import com.github.erfangc.marketmonitor.fundamentals.FundamentalsService
import com.github.erfangc.marketmonitor.fundamentals.models.Fundamental
import com.github.erfangc.marketmonitor.io.MongoDB.database
import com.github.erfangc.marketmonitor.mostRecentWorkingDay
import com.github.erfangc.marketmonitor.previousWorkingDay
import com.github.erfangc.marketmonitor.prices.PriceService
import com.github.erfangc.marketmonitor.tickers.TickerService
import com.github.erfangc.marketmonitor.yfinance.YFinanceService
import com.mongodb.client.model.Indexes
import org.litote.kmongo.getCollection
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@ExperimentalStdlibApi
class CompanyReturnAnalysisService(
        private val fundamentalsService: FundamentalsService,
        private val tickerService: TickerService,
        private val yFinanceService: YFinanceService,
        private val dailyMetricsService: DailyMetricsService,
        private val priceService: PriceService
) {

    private val log = LoggerFactory.getLogger(CompanyReturnAnalysisService::class.java)

    companion object {
        val companyReturnAnalysesCollection = database.getCollection<CompanyReturnAnalysis>()
    }

    fun performDailyAnalysis(date: LocalDate? = null, drop: Boolean? = false) {
        val date = date ?: LocalDate.now().previousWorkingDay()
        val top = 3000
        log.info("Performing expected return analysis in the top $top funds by market cap on $date")
        val rows = dailyMetricsService
                .getDailyMetrics(date)
                .sortedByDescending { it.marketcap }
                .take(top)
                .mapNotNull { dailyMetric ->
                    val ticker = dailyMetric.ticker
                    log.info("Analyzing expected returns for $ticker as of $date")
                    try {
                        val request = getCompanyReturnAnalysisRequest(ticker)
                        companyReturnAnalysis(request)
                    } catch (e: Exception) {
                        log.error("Unable to compute expected return analysis for $ticker on $date, message=${e.message}")
                        null
                    }
                }
        if (drop == true) {
            companyReturnAnalysesCollection.drop()
            companyReturnAnalysesCollection.createIndex(
                    Indexes.ascending(CompanyReturnAnalysis::date.name)
            )
        }
        val result = companyReturnAnalysesCollection.insertMany(rows)
        log.info("Saved ${result.insertedIds.size} rows into MongoDB collection ${CompanyReturnAnalysis::class.simpleName}")
    }

    fun getCompanyReturnAnalysisRequest(ticker: String): CompanyReturnAnalysisRequest {
        val rows = yFinanceService.getEarningsEstimate(ticker)
        val row = rows.find { it["Earnings Estimate"] == "Avg. Estimate" } ?: emptyMap()

        val now = LocalDate.now().previousWorkingDay()
        val shortTermEpsGrowths = try {
            tryParsingEpsForCurrentFY(currentFy = now, row = row)
        } catch (e: IllegalStateException) {
            tryParsingEpsForCurrentFY(currentFy = now.plusYears(1), row = row)
        }

        return CompanyReturnAnalysisRequest(
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
    fun companyReturnAnalysis(request: CompanyReturnAnalysisRequest): CompanyReturnAnalysis {
        val date = request.date ?: LocalDate.now().mostRecentWorkingDay()
        val ticker = request.ticker
        val mrt = fundamentalsService.getMRT(ticker = ticker, notAfter = date)
        val longTermGrowth = request.longTermGrowth

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

        val meta = tickerService.getTicker(ticker) ?: error("Unable to find information for $ticker")

        return CompanyReturnAnalysis(
                _id = "$ticker:$date",
                meta = meta,
                date = date,
                ticker = request.ticker,
                eps = eps,
                bvps = tbvps,
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