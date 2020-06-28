package com.github.erfangc.marketmonitor.fundamentals

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.erfangc.marketmonitor.fundamentals.models.Fundamental
import com.github.erfangc.marketmonitor.fundamentals.models.FundamentalRow
import com.github.erfangc.marketmonitor.io.MongoDB.database
import com.mongodb.client.model.Indexes
import com.vhl.blackmo.grass.dsl.grass
import org.litote.kmongo.*
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@ExperimentalStdlibApi
class FundamentalsService {

    companion object {
        val fundamentals = database.getCollection<FundamentalRow>()
    }

    private val log = LoggerFactory.getLogger(FundamentalsService::class.java)

    @Cacheable("getMrq")
    fun getMrq(ticker: String, notAfter: LocalDate?): List<Fundamental> {
        val notAfter = notAfter ?: LocalDate.now()
        val reportperiod = FundamentalRow::fundamental / Fundamental::reportperiod
        val dimension = FundamentalRow::fundamental / Fundamental::dimension
        val tickerProperty = FundamentalRow::fundamental / Fundamental::ticker
        return fundamentals.find(
                and(
                        reportperiod lte notAfter,
                        dimension eq "MRQ",
                        tickerProperty eq ticker
                )
        ).toList().sortedBy { it.fundamental.reportperiod }.map { it.fundamental }
    }

    fun load() {
        val fileName = "/Users/erfangchen/Downloads/SHARADAR_SF1_0902195e140925ca23756cd43d9f89ae.csv"
        val fundamentals = fundamentals

        fundamentals.drop()
        fundamentals.createIndex(Indexes.ascending(
                (FundamentalRow::fundamental / Fundamental::reportperiod).name
        ))
        fundamentals.createIndex(Indexes.compoundIndex(Indexes.hashed(
                (FundamentalRow::fundamental / Fundamental::ticker).name
        )))

        csvReader()
                .open(fileName) {
                    val seq = readAllWithHeaderAsSequence()
                    grass<Fundamental>()
                            .harvest(seq)
                            .chunked(5000)
                            .forEachIndexed { idx, chunk ->
                                val rows = chunk.map {
                                    FundamentalRow(
                                            _id = "${it.ticker}:${it.dimension}:${it.datekey}:${it.reportperiod}",
                                            fundamental = it
                                    )
                                }
                                fundamentals.insertMany(rows)
                                log.info("Loaded batch $idx into MongoDB")
                            }
                }
    }
}