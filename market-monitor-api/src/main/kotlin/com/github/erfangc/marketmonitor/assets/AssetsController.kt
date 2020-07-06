package com.github.erfangc.marketmonitor.assets

import com.github.erfangc.marketmonitor.assets.models.Asset
import org.springframework.web.bind.annotation.*

@ExperimentalStdlibApi
@RestController
@RequestMapping("assets")
class AssetsController(private val assetsService: AssetsService) {

    @ExperimentalStdlibApi
    @PostMapping
    fun bootstrap() {
        assetsService.bootstrap()
    }

    @GetMapping("{ticker}")
    fun getTicker(@PathVariable ticker: String): Asset? {
        return assetsService.getTicker(ticker)
    }

    @GetMapping("_match")
    fun matchTicker(@RequestParam term: String): List<Asset> {
        return assetsService.matchTicker(term)
    }
}