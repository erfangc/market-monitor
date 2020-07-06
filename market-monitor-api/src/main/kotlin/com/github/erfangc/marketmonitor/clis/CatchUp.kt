package com.github.erfangc.marketmonitor.clis

import ch.qos.logback.classic.Level
import com.github.erfangc.marketmonitor.dailymetrics.DailyMetricsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CatchUp

private val log = LoggerFactory.getLogger(CatchUp::class.java)
private val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger

@ExperimentalStdlibApi
fun main() {
    rootLogger.level = Level.INFO
    val dailyMetricsService = DailyMetricsService()
    log.info("Catching up with source")
    dailyMetricsService.catchUp()
}