package com.github.erfangc.marketmonitor.analysis.companyreturn

import com.github.erfangc.marketmonitor.alphavantage.AlphaVantageService
import com.github.erfangc.marketmonitor.analysis.companyreturn.models.*
import com.github.erfangc.marketmonitor.dailymetrics.DailyMetricsService
import com.github.erfangc.marketmonitor.fundamentals.FundamentalsService
import com.github.erfangc.marketmonitor.fundamentals.models.Fundamental
import com.github.erfangc.marketmonitor.io.MongoDB.database
import com.github.erfangc.marketmonitor.mostRecentWorkingDay
import com.github.erfangc.marketmonitor.previousWorkingDay
import com.github.erfangc.marketmonitor.tickers.TickerService
import com.github.erfangc.marketmonitor.yfinance.YFinanceService
import org.litote.kmongo.getCollection
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.pow

@Service
@ExperimentalStdlibApi
class CompanyReturnAnalysisService(
        private val fundamentalsService: FundamentalsService,
        private val tickerService: TickerService,
        private val yFinanceService: YFinanceService,
        private val dailyMetricsService: DailyMetricsService,
        private val alphaVantageService: AlphaVantageService
) {

    private val log = LoggerFactory.getLogger(CompanyReturnAnalysisService::class.java)

    internal data class PvFromShortTerm(val pv: Double, val valuationDate: LocalDate, val eps: Double)

    companion object {
        val companyReturnAnalysesCollection = database.getCollection<CompanyReturnAnalysis>()
    }

    fun saveDailyAnalysis(date: LocalDate? = null) {
        val date = date ?: LocalDate.now().previousWorkingDay()
        log.info("Performing expected return analysis in the top 500 funds by market cap on $date")
        val rows = dailyMetricsService
                .getDailyMetrics(date)
                .sortedByDescending { it.marketcap }
                .take(500)
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
        companyReturnAnalysesCollection.drop()
        val result = companyReturnAnalysesCollection.insertMany(rows)
        log.info("Saved ${result.insertedIds.size} rows into MongoDB collection ${CompanyReturnAnalysis::class.simpleName}")
    }

    fun getCompanyReturnAnalysisRequest(ticker: String): CompanyReturnAnalysisRequest {
        val rows = yFinanceService.getEarningsEstimate(ticker)
        val row = rows.find { it["Earnings Estimate"] == "Avg. Estimate" } ?: emptyMap()

        val now = LocalDate.now()
        val shortTermEpsGrowths = try {
            tryParsingEpsForCurrentFY(currentFy = now, row = row)
        } catch (e: IllegalArgumentException) {
            tryParsingEpsForCurrentFY(currentFy = now.plusYears(1), row = row)
        }

        return CompanyReturnAnalysisRequest(
                ticker = ticker,
                date = now,
                longTermGrowth = 0.03,
                shortTermEpsGrowths = shortTermEpsGrowths,
                price = alphaVantageService.getPrice(ticker)
        )

    }

    private fun yearFrac(from: LocalDate, to: LocalDate) = ChronoUnit.DAYS.between(from, to) / 365.2425

    private fun tryParsingEpsForCurrentFY(
            currentFy: LocalDate,
            row: Map<String, String>
    ): List<ShortTermEpsGrowth> {
        val forYear = { date: LocalDate ->
            val adjective = if (date.year == currentFy.year) "Current" else "Next"
            "$adjective Year (${date.year})"
        }
        val nextYear = currentFy.plusYears(1)
        return listOf(currentFy, nextYear).map {
            ShortTermEpsGrowth(
                    date = it,
                    eps = row[forYear(it)]?.toDouble() ?: throw IllegalArgumentException()
            )
        }
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
        val bvps = tbvps(fundamental) ?: 0.0
        val fx = fundamental.fxusd ?: 1.0
        val price = request.price ?: fundamental.price ?: error("Price is unavailable for $ticker on $date")

        if (price == 0.0) {
            error("Price for $ticker cannot be 0.0")
        }

        val shortTermEpsGrowth = request.shortTermEpsGrowths.map {
            it.copy(eps = it.eps / fx)
        }

        val fn = goalSeekFunctionGenerator(
                inputs = GoalSeekFunctionInputs(
                        date = date,
                        bvps = bvps,
                        eps = eps,
                        price = price,
                        longTermGrowth = longTermGrowth,
                        shortTermEpsGrowths = shortTermEpsGrowth
                )
        )

        val discountRate = bisection(initialMin = longTermGrowth, initialMax = 1.0, fn = fn)

        val pricingFunctionOutputs = pricingFunction(
                PricingFunctionInputs(
                        date = date,
                        bvps = bvps,
                        eps = eps,
                        longTermGrowth = longTermGrowth,
                        discountRate = discountRate,
                        shortTermEpsGrowth = shortTermEpsGrowth
                )
        )

        val meta = tickerService.getTicker(ticker) ?: error("Unable to find information for $ticker")

        return CompanyReturnAnalysis(
                _id = "$ticker:$date",
                meta = meta,
                date = date,
                ticker = request.ticker,
                eps = eps,
                bvps = bvps,
                priceToEarning = price / eps,
                shortTermEpsGrowths = shortTermEpsGrowth,
                discountRate = discountRate,
                longTermGrowth = longTermGrowth,
                pricingFunctionOutputs = pricingFunctionOutputs
        )
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

    /**
     * Creates a function that only accept discount factor as an argument (everything else is pre-computed via [inputs])
     *
     * The returned function accepts discount rate and produce the difference between predicted and actual price. It is
     * this method that the [bisection] method will operate on to find the root of
     *
     */
    private fun goalSeekFunctionGenerator(inputs: GoalSeekFunctionInputs): (Double) -> Double {
        return { discountRate ->
            pricingFunction(
                    PricingFunctionInputs(
                            date = inputs.date,
                            eps = inputs.eps,
                            bvps = inputs.bvps,
                            discountRate = discountRate,
                            longTermGrowth = inputs.longTermGrowth,
                            shortTermEpsGrowth = inputs.shortTermEpsGrowths
                    )
            ).price - inputs.price
        }
    }

    /**
     * Implements the bisection root finding method
     * @param min the lower bound of guess
     * @param max the upper bound for guess
     */
    private fun bisection(initialMin: Double, initialMax: Double, fn: (Double) -> Double): Double {
        //
        // use bi-section method on fn(r)
        // assume r must be between 0% and 100%
        //
        val maxIter = 1000
        val tolerance = 0.001
        var iter = 0
        var epislon: Double
        var min = initialMin
        var max = initialMax
        var mid: Double
        do {
            mid = (min + max) / 2.0
            epislon = fn(mid)
            if (epislon < 0) {
                max = mid
            } else if (epislon > 0) {
                min = mid
            } else {
                break
            }
            iter++
        } while (iter < maxIter && abs(epislon) > tolerance)

        if (iter == maxIter && abs(epislon) > tolerance) {
            val message = "Goal seek algorithm did not converge within $iter iterations, epislon=$epislon"
            log.error(message)
            error(message)
        }

        return mid
    }

    /**
     * Computes the theoretical price of a security
     * based on assumptions (current book value) and growth trajectory
     *
     * here discount rate is an input
     */
    private fun pricingFunction(inputs: PricingFunctionInputs): PricingFunctionOutputs {
        val bvps = inputs.bvps
        val date = inputs.date
        val discountRate = inputs.discountRate
        val eps = inputs.eps
        val longTermGrowth = inputs.longTermGrowth
        val shortTermEpsEpsGrowth = inputs.shortTermEpsGrowth

        val pvsFromShortTerm = shortTermEpsEpsGrowth.fold(listOf<PvFromShortTerm>()) { acc, stg ->
            val stgDate = stg.date
            val yearFrac = yearFrac(date, stgDate)
            val discountFactor = 1 / (1 + discountRate).pow(yearFrac)
            val newEps = stg.eps
            acc + PvFromShortTerm(pv = newEps * discountFactor, valuationDate = stgDate, eps = newEps)
        }

        val lastEps = if (pvsFromShortTerm.isEmpty()) eps else pvsFromShortTerm.last().eps
        val terminalValue = lastEps / (discountRate - longTermGrowth)
        val lastValuationDate = if (pvsFromShortTerm.isEmpty()) date else pvsFromShortTerm.last().valuationDate
        val terminalDiscountFactor = 1 / (1 + discountRate).pow(yearFrac(date, lastValuationDate))
        val contributionFromTerminalValue = terminalValue * terminalDiscountFactor
        val contributionFromShortTerm = pvsFromShortTerm.sumByDouble { it.pv }
        val discountedBvps = bvps * terminalDiscountFactor

        // share price = current book value + short-term growth + terminal value
        val price = discountedBvps + contributionFromShortTerm + contributionFromTerminalValue

        val contributionFromCurrentEarnings = eps / discountRate
        val contributionFromGrowth = price - contributionFromCurrentEarnings - discountedBvps

        return PricingFunctionOutputs(
                price = price,
                contributionFromBvps = discountedBvps,
                contributionFromCurrentEarnings = contributionFromCurrentEarnings,
                contributionFromGrowth = contributionFromGrowth,
                contributionFromShortTerm = contributionFromShortTerm,
                contributionFromTerminalValue = contributionFromTerminalValue
        )
    }

}