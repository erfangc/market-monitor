package com.github.erfangc.marketmonitor.tickers

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("tickers")
class TickersLoaderController(private val tickersService: TickerService) {
    @ExperimentalStdlibApi
    @PostMapping
    fun loadTickers() {
        tickersService.loadTickers()
    }

    @GetMapping("{ticker}")
    fun getTicker(@PathVariable ticker: String): Ticker? {
        return tickersService.getTicker(ticker)
    }
}