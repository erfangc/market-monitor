package com.github.erfangc.marketmonitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MarketMonitorApplication

fun main(args: Array<String>) {
	runApplication<MarketMonitorApplication>(*args)
}
