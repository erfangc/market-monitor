package com.github.erfangc.marketmonitor.yfinance

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("y-finance")
class YFinanceController(private val yFinanceService: YFinanceService) {
    @GetMapping("earnings-estimate/{ticker}")
    fun getEarningsEstimate(@PathVariable ticker: String): List<Map<String, String>> {
        return yFinanceService.getEarningsEstimate(ticker)
    }
}