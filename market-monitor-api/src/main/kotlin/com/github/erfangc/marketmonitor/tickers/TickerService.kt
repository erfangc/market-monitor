package com.github.erfangc.marketmonitor.tickers

import com.github.erfangc.marketmonitor.io.MongoDB
import com.github.erfangc.marketmonitor.quandl.QuandlService
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes
import com.vhl.blackmo.grass.dsl.grass
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@ExperimentalStdlibApi
@Service
class TickerService(
        private val quandlService: QuandlService
) {

    private val tickersCollection = MongoDB.database.getCollection<TickerRow>()
    private val log = LoggerFactory.getLogger(TickerService::class.java)

    @ExperimentalStdlibApi
    fun loadTickers() {
        tickersCollection.drop()
        tickersCollection.createIndex(Indexes.hashed((TickerRow::ticker / Ticker::ticker).name))
        tickersCollection.createIndex(Indexes.text((TickerRow::ticker / Ticker::ticker).name))
        quandlService.exportQuandl(publisher = "SHARADAR", table = "TICKERS") { response ->
            val rows = grass<Ticker>()
                    .harvest(response.asList())
                    .filter { it.table == "SF1" }
                    .map {
                        TickerRow(_id = "${it.table}:${it.permaticker}:${it.ticker}", ticker = it)
                    }
            if (rows.isNotEmpty()) {
                tickersCollection.insertMany(rows)
                log.info("Inserted ${rows.size} rows into the ${Ticker::class.simpleName} collection")
            }
        }
    }

    @Cacheable("getTickers")
    fun getTicker(ticker: String): Ticker? {
        return tickersCollection.findOne(TickerRow::ticker / Ticker::ticker eq ticker)?.ticker
    }

    @Cacheable("matchTicker")
    fun matchTicker(term: String): List<Ticker> {
        return tickersCollection.find(Filters.text(term)).map { it.ticker }.toList()
    }

}