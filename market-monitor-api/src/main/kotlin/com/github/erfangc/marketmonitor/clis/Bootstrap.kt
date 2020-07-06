package com.github.erfangc.marketmonitor.clis

import ch.qos.logback.classic.Level
import com.github.erfangc.marketmonitor.analysis.company.CompanyAnalysisService
import com.github.erfangc.marketmonitor.assets.AssetsService
import com.github.erfangc.marketmonitor.dailymetrics.DailyMetricsService
import com.github.erfangc.marketmonitor.fundamentals.FundamentalsService
import com.github.erfangc.marketmonitor.io.MongoDB.database
import com.github.erfangc.marketmonitor.prices.PriceService
import com.github.erfangc.marketmonitor.yfinance.YFinanceService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BootstrapExternalData

private val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
private val log = LoggerFactory.getLogger(BootstrapExternalData::class.java)

@ExperimentalStdlibApi
fun main() {
    rootLogger.level = Level.INFO
    val fundamentalsService = FundamentalsService()
    val dailyMetricsService = DailyMetricsService()
    val assetsService = AssetsService()
    val companyAnalysisService = CompanyAnalysisService(
            fundamentalsService = fundamentalsService,
            assetsService = assetsService,
            priceService = PriceService(),
            dailyMetricsService = dailyMetricsService,
            yFinanceService = YFinanceService()
    )
    log.info("Bootstrapping external data from scratch, dropping database ${database.name}")
    database.drop()
    fundamentalsService.bootstrap()
    dailyMetricsService.bootstrap()
    assetsService.bootstrap()
    companyAnalysisService.bootstrap()
    log.info("Finished bootstrapping database ${database.name}")
}