package com.github.erfangc.marketmonitor.tickers

import org.springframework.web.bind.annotation.*

@ExperimentalStdlibApi
@RestController
@RequestMapping("tickers")
class TickerController(private val tickersService: TickerService) {

    @ExperimentalStdlibApi
    @PostMapping
    fun loadTickers() {
        tickersService.loadTickers()
    }

    @GetMapping("{ticker}")
    fun getTicker(@PathVariable ticker: String): Ticker? {
        return tickersService.getTicker(ticker)
    }

    @GetMapping("_match")
    fun matchTicker(@RequestParam term: String): List<Ticker> {
        return tickersService.matchTicker(term)
    }
}