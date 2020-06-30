package com.github.erfangc.marketmonitor.fundamentals

import com.github.erfangc.marketmonitor.fundamentals.models.Fundamental
import com.github.erfangc.marketmonitor.fundamentals.models.FundamentalRow
import com.github.erfangc.marketmonitor.io.MongoDB.database
import com.github.erfangc.marketmonitor.quandl.QuandlService
import com.mongodb.client.model.Indexes
import com.vhl.blackmo.grass.dsl.grass
import org.litote.kmongo.*
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@ExperimentalStdlibApi
class FundamentalsService(
        private val quandlService: QuandlService
) {

    companion object {
        val fundamentals = database.getCollection<FundamentalRow>()
    }

    private val log = LoggerFactory.getLogger(FundamentalsService::class.java)

    @Cacheable("getMRQ")
    fun getMRQ(ticker: String, notAfter: LocalDate?): List<Fundamental> {
        val dimension = "MRQ"
        return getForDimension(notAfter, dimension, ticker)
    }

    @Cacheable("getMRY")
    fun getMRY(ticker: String, notAfter: LocalDate?): List<Fundamental> {
        val dimension = "MRY"
        return getForDimension(notAfter, dimension, ticker)
    }

    @Cacheable("getMRT")
    fun getMRT(ticker: String, notAfter: LocalDate?): List<Fundamental> {
        val dimension = "MRT"
        return getForDimension(notAfter, dimension, ticker)
    }

    fun load() {
        fundamentals.drop()
        fundamentals.createIndex(Indexes.ascending(
                (FundamentalRow::fundamental / Fundamental::reportperiod).name
        ))
        fundamentals.createIndex(Indexes.compoundIndex(Indexes.hashed(
                (FundamentalRow::fundamental / Fundamental::ticker).name
        )))
        quandlService.exportQuandl(publisher = "SHARADAR", table = "SF1") { response ->
            val fundamentalRows = grass<Fundamental>()
                    .harvest(response.asList())
                    .map {
                        FundamentalRow(
                                _id = "${it.ticker}:${it.dimension}:${it.datekey}:${it.reportperiod}",
                                fundamental = it
                        )
                    }
            val result = fundamentals.insertMany(fundamentalRows)
            log.info("Inserted ${result.insertedIds.size} rows in the ${FundamentalRow::class.simpleName} collection")
        }
    }

    private fun getForDimension(
            notAfter: LocalDate?,
            dimension: String,
            ticker: String
    ): List<Fundamental> {
        val notAfter = notAfter ?: LocalDate.now()
        val reportperiodProperty = FundamentalRow::fundamental / Fundamental::reportperiod
        val dimensionProperty = FundamentalRow::fundamental / Fundamental::dimension
        val tickerProperty = FundamentalRow::fundamental / Fundamental::ticker
        return fundamentals.find(
                and(
                        reportperiodProperty lte notAfter,
                        dimensionProperty eq dimension,
                        tickerProperty eq ticker
                )
        ).toList().sortedBy { it.fundamental.reportperiod }.map { it.fundamental }
    }
}
