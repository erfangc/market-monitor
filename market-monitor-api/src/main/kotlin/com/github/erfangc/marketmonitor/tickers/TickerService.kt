package com.github.erfangc.marketmonitor.tickers

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.erfangc.marketmonitor.io.MongoDB
import com.mongodb.client.model.Indexes
import com.vhl.blackmo.grass.dsl.grass
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class TickerService {

    private val tickersCollection = MongoDB.database.getCollection<TickerRow>()
    private val log = LoggerFactory.getLogger(TickerService::class.java)

    @ExperimentalStdlibApi
    fun loadTickers() {
        tickersCollection.drop()
        tickersCollection.createIndex(Indexes.hashed((TickerRow::ticker / Ticker::ticker).name))
        csvReader().open("/Users/erfangchen/Downloads/SHARADAR_TICKERS_6cc728d11002ab9cb99aa8654a6b9f4e.csv") {
            grass<Ticker>()
                    .harvest(readAllWithHeaderAsSequence())
                    .chunked(10000)
                    .forEachIndexed {
                        idx, chunk ->
                        val rows = chunk.map {
                            TickerRow(_id = "${it.table}:${it.permaticker}:${it.ticker}", ticker = it)
                        }
                        tickersCollection.insertMany(rows)
                        log.info("Inserted ${rows.size} from batch $idx into MongoDB")
                    }
        }
    }

    @Cacheable("tickers")
    fun getTicker(ticker: String): Ticker? {
        return tickersCollection.findOne(TickerRow::ticker / Ticker::ticker eq ticker)?.ticker
    }

}