package com.github.erfangc.marketmonitor.analysis.companyreturn

import com.github.erfangc.marketmonitor.alphavantage.AlphaVantageService
import com.github.erfangc.marketmonitor.analysis.companyreturn.models.*
import com.github.erfangc.marketmonitor.fundamentals.FundamentalsService
import com.github.erfangc.marketmonitor.mostRecentWorkingDay
import com.github.erfangc.marketmonitor.yfinance.YFinanceService
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
        private val yFinanceService: YFinanceService,
        private val alphaVantageService: AlphaVantageService
) {

    private fun yearFrac(from: LocalDate, to: LocalDate) = ChronoUnit.DAYS.between(from, to) / 365.2425

    private val log = LoggerFactory.getLogger(CompanyReturnAnalysisService::class.java)

    internal data class PvFromShortTerm(val pv: Double, val valuationDate: LocalDate, val eps: Double)

    fun getCompanyReturnAnalysisRequest(ticker: String): CompanyReturnAnalysisRequest {
        val rows = yFinanceService.getEarningsEstimate(ticker)

        val forYear = { date: LocalDate ->
            val adjective = if (date.year == LocalDate.now().year) "Current" else "Next"
            "$adjective Year (${date.year})"
        }

        val row = rows
                .find { it["Earnings Estimate"] == "Avg. Estimate" } ?: emptyMap()

        val currentYear = LocalDate.now()
        val nextYear = currentYear.plusYears(1)
        val shortTermEpsGrowths = listOf(currentYear, nextYear).map {
            ShortTermEpsGrowth(
                    date = it,
                    eps = row[forYear(it)]?.toDoubleOrNull()
            )
        }

        val date = LocalDate.now()
        val price = alphaVantageService.getPrice(ticker)

        return CompanyReturnAnalysisRequest(
                ticker = ticker,
                date = date,
                longTermGrowth = 0.03,
                shortTermEpsGrowths = shortTermEpsGrowths,
                price = price
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
        val shortTermEpsGrowth = request.shortTermEpsGrowths

        if (mrt.isEmpty()) {
            error("Dimension MRT Fundamental data is unavailable for $ticker on $date")
        }
        val fundamental = mrt.last()

        val eps = fundamental.eps ?: error("EPS is unavailable for $ticker on $date")
        val bvps = fundamental.bvps ?: error("BVPS is unavailable for $ticker on $date")
        val price = request.price ?: fundamental.price ?: error("Price is unavailable for $ticker on $date")

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

        return CompanyReturnAnalysis(
                date = date,
                ticker = request.ticker,
                eps = eps,
                bvps = bvps,
                priceToEarning = price / eps,
                shortTermEpsGrowths = shortTermEpsGrowth,
                expectedReturn = discountRate,
                longTermGrowth = longTermGrowth,
                pricingFunctionOutputs = pricingFunctionOutputs
        )
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
            log.error("Goal seek algorithm did not converge within $iter iterations, epislon=$epislon")
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
            val lastEps = if (acc.isEmpty()) eps else acc.last().eps

            val newEps = if (stg.growthRate != null) lastEps * (1 + stg.growthRate) else stg.eps
                    ?: error("Either growthRate or eps must be provided")

            acc + PvFromShortTerm(pv = newEps * discountFactor, valuationDate = stgDate, eps = newEps)
        }

        val lastEps = if (pvsFromShortTerm.isEmpty()) eps else pvsFromShortTerm.last().eps
        val terminalValue = lastEps / (discountRate - longTermGrowth)
        val lastValuationDate = if (pvsFromShortTerm.isEmpty()) date else pvsFromShortTerm.last().valuationDate
        val terminalDiscountFactor = 1 / (1 + discountRate).pow(yearFrac(date, lastValuationDate))
        val contributionFromTerminalValue = terminalValue * terminalDiscountFactor
        val contributionFromShortTerm = pvsFromShortTerm.sumByDouble { it.pv }

        // share price = current book value + short-term growth + terminal value
        val price = bvps + contributionFromShortTerm + contributionFromTerminalValue

        val contributionFromCurrentEarnings = eps / discountRate
        val contributionFromGrowth = price - contributionFromCurrentEarnings - bvps

        return PricingFunctionOutputs(
                price = price,
                contributionFromBvps = bvps,
                contributionFromCurrentEarnings = contributionFromCurrentEarnings,
                contributionFromGrowth = contributionFromGrowth,
                contributionFromShortTerm = contributionFromShortTerm,
                contributionFromTerminalValue = contributionFromTerminalValue
        )
    }

}